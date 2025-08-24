package com.unisew.server.services.implementors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.AccountRequestRepo;
import com.unisew.server.repositories.CustomerRepo;
import com.unisew.server.repositories.WalletRepo;
import com.unisew.server.requests.CreatePartnerAccountRequestRequest;
import com.unisew.server.requests.EncryptPartnerDataRequest;
import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AuthService;
import com.unisew.server.services.JWTService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.MapUtils;
import com.unisew.server.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    @Value("${jwt.expiration.access-token}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshExpiration;

    @Value("${partner.algorithm}")
    private String ALGORITHM;

    @Value("${partner.key}")
    private String SECRET_KEY;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AccountRepo accountRepo;

    private final CustomerRepo customerRepo;

    private final WalletRepo walletRepo;

    private final AccountRequestRepo accountRequestRepo;

    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request, HttpServletResponse response) {
        Account account = accountRepo.findByEmail(request.getEmail()).orElse(null);
        AccountRequest accountRequest = accountRequestRepo.findByEmail(request.getEmail()).orElse(null);

        if(accountRequest != null){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This email is requested to be a partner", null);
        }

        if(request.getEmail() == null || request.getEmail().isEmpty()){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Email is required", null);
        }

        if (account == null) {
            return register(request, response);
        }

        if (account.getStatus().equals(Status.ACCOUNT_INACTIVE)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account is banned", null);
        }

        String access = jwtService.generateAccessToken(account);
        String refresh = jwtService.generateRefreshToken(account);

        CookieUtil.createCookies(response, access, refresh, accessExpiration, refreshExpiration);

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", buildAccountData(account));
    }

    private ResponseEntity<ResponseObject> register(LoginRequest request, HttpServletResponse response) {

        Account account = accountRepo.save(
                Account.builder()
                        .email(request.getEmail())
                        .role(Role.SCHOOL)
                        .registerDate(LocalDate.now())
                        .status(Status.ACCOUNT_ACTIVE)
                        .build()
        );

        Customer customer = customerRepo.save(
                Customer.builder()
                        .account(account)
                        .businessName("N/A")
                        .address("N/A")
                        .taxCode("N/A")
                        .name(request.getName())
                        .phone("N/A")
                        .avatar(request.getAvatar())
                        .build()
        );

        Wallet wallet = walletRepo.save(
                Wallet.builder()
                        .account(account)
                        .balance(0)
                        .pendingBalance(0)
                        .cardNumber("N/A")
                        .cardName("N/A")
                        .cardExpiredDate(null)
                        .build()
        );

        account.setCustomer(customer);
        account.setWallet(wallet);
        accountRepo.save(account);

        String access = jwtService.generateAccessToken(account);
        String refresh = jwtService.generateRefreshToken(account);

        CookieUtil.createCookies(response, access, refresh, accessExpiration, refreshExpiration);

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", buildAccountData(account));
    }

    private Map<String, Object> buildAccountData(Account account) {
        Map<String, Object> accountData = new HashMap<>();
        accountData.put("email", account.getEmail());
        accountData.put("registerDate", account.getRegisterDate());
        accountData.put("role", account.getRole().getValue().toLowerCase());
        if (!account.getRole().equals(Role.ADMIN)) {
            accountData.put("customer", buildCustomerData(account));
            if (!account.getRole().equals(Role.SCHOOL)) {
                accountData.put("partner", buildPartnerData(account));
            }
        }
        return accountData;
    }

    private Map<String, Object> buildCustomerData(Account account) {
        Customer customer = account.getCustomer();
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("address", customer.getAddress());
        customerData.put("taxCode", customer.getTaxCode());
        customerData.put("name", customer.getName());
        customerData.put("business", customer.getBusinessName());
        customerData.put("phone", customer.getPhone());
        customerData.put("avatar", customer.getAvatar());
        return customerData;
    }

    private Map<String, Object> buildPartnerData(Account account) {
        Partner partner = account.getCustomer().getPartner();
        Map<String, Object> partnerData = new HashMap<>();
        partnerData.put("outsidePreview", partner.getOutsidePreview());
        partnerData.put("insidePreview", partner.getInsidePreview());
        partnerData.put("startTime", partner.getStartTime());
        partnerData.put("endTime", partner.getEndTime());
        partnerData.put("rating", partner.getRating());
        partnerData.put("busy", partner.isBusy());
        return partnerData;
    }

    @Override
    public ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response) {

        Account currentAcc = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (currentAcc == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No user found", null);
        }

        String newAccess = jwtService.generateAccessToken(currentAcc);

        String newRefresh = jwtService.generateRefreshToken(currentAcc);

        CookieUtil.createCookies(response, newAccess, newRefresh, accessExpiration, refreshExpiration);

        return ResponseBuilder.build(HttpStatus.OK, "Refresh access token successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> encryptPartnerData(EncryptPartnerDataRequest request) {
        try {
            Map<String, Object> dataToEncrypt = new HashMap<>();
            dataToEncrypt.put("email", request.getEmail());
            dataToEncrypt.put("role", request.getRole());
            dataToEncrypt.put("address", request.getAddress());
            dataToEncrypt.put("taxCode", request.getTaxCode());
            dataToEncrypt.put("phone", request.getPhone());

            long expirationTimeMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 hours in milliseconds
            dataToEncrypt.put("expirationTime", expirationTimeMillis);

            // Convert the map to a JSON string
            String jsonString = objectMapper.writeValueAsString(dataToEncrypt);

            // Encrypt the string
            String encryptedString = encrypt(jsonString);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("encryptData", encryptedString);

            System.out.println("Encrypt Data: " + encryptedString);

            return ResponseBuilder.build(HttpStatus.OK, "Data encrypted successfully", responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Something wrong", null);
        }
    }


    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createPartnerAccountRequest(CreatePartnerAccountRequestRequest request) {
        try {

            // Decrypt the string
            String decryptedString = decrypt(request.getEncryptedData().replaceAll(" ", "+"));
            if (decryptedString == null) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid encrypted data", null);
            }

            // Use a Map to deserialize the JSON, which includes the expirationTime
            Map<String, Object> decryptedDataMap = objectMapper.readValue(decryptedString, new TypeReference<Map<String, Object>>() {});

            //Check for Expiration
            if (decryptedDataMap.containsKey("expirationTime")) {
                long expirationTimeMillis = Long.parseLong(decryptedDataMap.get("expirationTime").toString());
                if (System.currentTimeMillis() >= expirationTimeMillis) {
                    return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "The request has expired", null);
                }
            } else {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Missing expiration timestamp", null);
            }

            EncryptPartnerDataRequest data = EncryptPartnerDataRequest.builder()
                    .email((String) decryptedDataMap.get("email"))
                    .role((String) decryptedDataMap.get("role"))
                    .address((String) decryptedDataMap.get("address"))
                    .taxCode((String) decryptedDataMap.get("taxCode"))
                    .phone((String) decryptedDataMap.get("phone"))
                    .build();

            String error = validateCreatePartnerAccountRequest(data);
            if (!error.isEmpty()) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
            }

            accountRequestRepo.save(
                    AccountRequest.builder()
                            .email(data.getEmail())
                            .role(Role.valueOf(data.getRole()))
                            .address(data.getAddress())
                            .taxCode(data.getTaxCode())
                            .phone(data.getPhone())
                            .status(Status.ACCOUNT_REQUEST_PENDING)
                            .build()
            );

            return ResponseBuilder.build(HttpStatus.CREATED, "Request sent", null);
        } catch (Exception e) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Something wrong", null);
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getNumberAccountRole() {

        List<Account> accounts = accountRepo.findAll().stream()
                .filter(a -> !a.getRole().equals(Role.ADMIN))
                .toList();
        if (accounts.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "No accounts found", null);
        }
        List<Account> schoolAccount = accounts.stream().filter(a -> a.getRole().equals(Role.SCHOOL)).toList();
        List<Account> designerAccount = accounts.stream().filter(a -> a.getRole().equals(Role.DESIGNER)).toList();
        List<Account> garmentAccount = accounts.stream().filter(a -> a.getRole().equals(Role.GARMENT)).toList();

        Map<String,Object> responseData = new HashMap<>();
        responseData.put("numberSchoolAccount", schoolAccount.size());
        responseData.put("numberDesignerAccount", designerAccount.size());
        responseData.put("numberGarmentAccount", garmentAccount.size());


        return ResponseBuilder.build(HttpStatus.OK, "Number of account", responseData);
    }

    private String validateCreatePartnerAccountRequest(EncryptPartnerDataRequest data) {
        if(data.getEmail() == null || data.getEmail().isEmpty()){
            return "Email is required";
        }

        if(accountRepo.existsByEmail(data.getEmail())){
            return "This email is already used";
        }

        if(accountRequestRepo.existsByEmail(data.getEmail())){
            return "This email is already requested";
        }

        if(data.getRole() == null || data.getRole().isEmpty()){
            return "Role is required";
        }

        if(data.getAddress() == null || data.getAddress().isEmpty()){
            return "Address is required";
        }

        if(data.getTaxCode() == null || data.getTaxCode().isEmpty()){
            return "Tax code is required";
        }

        if(data.getPhone() == null || data.getPhone().isEmpty()){
            return "Phone is required";
        }

        return "";
    }

    private SecretKeySpec getKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
