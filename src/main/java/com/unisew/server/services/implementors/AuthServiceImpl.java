package com.unisew.server.services.implementors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Customer;
import com.unisew.server.models.Partner;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.CustomerRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.PlatformConfigRepo;
import com.unisew.server.repositories.WalletRepo;
import com.unisew.server.requests.CreatePartnerAccountRequest;
import com.unisew.server.requests.EncryptPartnerDataRequest;
import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AuthService;
import com.unisew.server.services.JWTService;
import com.unisew.server.utils.CookieUtil;
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
import java.time.LocalTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final PartnerRepo partnerRepo;
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

    private final PlatformConfigRepo platformConfigRepo;

    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request, HttpServletResponse response) {
        Account account = accountRepo.findByEmail(request.getEmail().toLowerCase()).orElse(null);


        if (request.getEmail() == null || request.getEmail().isEmpty()) {
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
                        .email(request.getEmail().toLowerCase())
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
                        .cardOwner("hihi")
                        .bankAccountNumber("01234567789")
                        .bank("ABB")
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
        partnerData.put("depositPercentage", partner.getDepositPercentage());
        partnerData.put("shippingUID", partner.getCustomer().getAccount().getRole().equals(Role.GARMENT) ? partner.getShippingUid() : "");
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

            if (request.getAccountData() != null) {
                EncryptPartnerDataRequest.AccountData data = request.getAccountData();
                dataToEncrypt.put("email", data.getEmail().toLowerCase());
                dataToEncrypt.put("role", data.getRole());
            }

            if (request.getCustomerData() != null) {
                EncryptPartnerDataRequest.CustomerData data = request.getCustomerData();
                dataToEncrypt.put("address", data.getAddress());
                dataToEncrypt.put("ward", data.getWard());
                dataToEncrypt.put("district", data.getDistrict());
                dataToEncrypt.put("province", data.getProvince());
                dataToEncrypt.put("taxCode", data.getTaxCode());
                dataToEncrypt.put("name", data.getName());
                dataToEncrypt.put("businessName", data.getBusinessName());
                dataToEncrypt.put("phone", data.getPhone());
                dataToEncrypt.put("avatar", data.getAvatar());
            }

            if (request.getPartnerData() != null) {
                EncryptPartnerDataRequest.PartnerData data = request.getPartnerData();
                dataToEncrypt.put("startTime", data.getStartTime());
                dataToEncrypt.put("endTime", data.getEndTime());
            }

            if (request.getWalletData() != null) {
                EncryptPartnerDataRequest.WalletData data = request.getWalletData();
                dataToEncrypt.put("bank", data.getBank());
                dataToEncrypt.put("bankAccountNumber", data.getBankAccountNumber());
                dataToEncrypt.put("cardOwner", data.getCardOwner());
            }

            if(request.getStoreData() != null){
                EncryptPartnerDataRequest.StoreData data = request.getStoreData();
                dataToEncrypt.put("districtId", data.getDistrictId());
                dataToEncrypt.put("wardCode", data.getWardCode());
                dataToEncrypt.put("address", data.getAddress());
                dataToEncrypt.put("storeName", data.getName());
                dataToEncrypt.put("phone", data.getPhone());
            }

            // Set expiration time
            long expirationTimeMillis = System.currentTimeMillis() + (5 * 60 * 1000); // 5 minutes in milliseconds
            dataToEncrypt.put("expirationTime", expirationTimeMillis);

            // Exchange map to JSON String
            String jsonString = objectMapper.writeValueAsString(dataToEncrypt);

            // Encrypt string
            String encryptedString = encrypt(jsonString);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("encryptData", encryptedString);

            return ResponseBuilder.build(HttpStatus.OK, "Data encrypted successfully", responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Something wrong", null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createPartnerAccount(CreatePartnerAccountRequest request) {
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

            EncryptPartnerDataRequest.AccountData accountData = EncryptPartnerDataRequest.AccountData.builder()
                    .email(((String) decryptedDataMap.get("email")).toLowerCase())
                    .role((String) decryptedDataMap.get("role"))
                    .build();

            EncryptPartnerDataRequest.CustomerData customerData = EncryptPartnerDataRequest.CustomerData.builder()
                    .address((String) decryptedDataMap.get("address"))
                    .ward((String) decryptedDataMap.get("ward"))
                    .district((String) decryptedDataMap.get("district"))
                    .province((String) decryptedDataMap.get("province"))
                    .taxCode((String) decryptedDataMap.get("taxCode"))
                    .name((String) decryptedDataMap.get("name"))
                    .businessName((String) decryptedDataMap.get("businessName"))
                    .phone((String) decryptedDataMap.get("phone"))
                    .avatar((String) decryptedDataMap.get("avatar"))
                    .build();

            EncryptPartnerDataRequest.PartnerData partnerData = EncryptPartnerDataRequest.PartnerData.builder()
                    .startTime(LocalTime.parse((String) decryptedDataMap.get("startTime")))
                    .endTime(LocalTime.parse((String) decryptedDataMap.get("endTime")))
                    .build();

            EncryptPartnerDataRequest.WalletData walletData = EncryptPartnerDataRequest.WalletData.builder()
                    .bank((String) decryptedDataMap.get("bank"))
                    .bankAccountNumber((String) decryptedDataMap.get("bankAccountNumber"))
                    .cardOwner((String) decryptedDataMap.get("cardOwner"))
                    .build();

            EncryptPartnerDataRequest.StoreData storeData = EncryptPartnerDataRequest.StoreData.builder()
                    .districtId((Integer) decryptedDataMap.get("districtId"))
                    .wardCode((Integer) decryptedDataMap.get("wardCode"))
                    .address((String) decryptedDataMap.get("address"))
                    .name((String) decryptedDataMap.get("storeName"))
                    .phone((String) decryptedDataMap.get("phone"))
                    .build();

            String error = validateCreatePartnerAccountRequest(accountData);
            if (!error.isEmpty()) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
            }

            Account account = accountRepo.save(
                    Account.builder()
                            .email(accountData.getEmail())
                            .role(Role.valueOf(accountData.getRole()))
                            .registerDate(LocalDate.now())
                            .status(Status.ACCOUNT_ACTIVE)
                            .build()
            );
            Customer customer = customerRepo.save(
                    Customer.builder()
                            .account(account)
                            .address(customerData.getAddress() + ", " + customerData.getWard() + ", " + customerData.getDistrict() + ", " + customerData.getProvince())
                            .taxCode(customerData.getTaxCode())
                            .name(customerData.getName())
                            .businessName(customerData.getBusinessName())
                            .phone(customerData.getPhone())
                            .avatar(customerData.getAvatar())
                            .build()
            );
            Partner partner = partnerRepo.save(
                    Partner.builder()
                            .customer(customer)
                            .outsidePreview("")
                            .insidePreview("")
                            .shippingUid("")
                            .startTime(partnerData.getStartTime())
                            .endTime(partnerData.getEndTime())
                            .rating(0)
                            .depositPercentage(0)
                            .build()
            );
            walletRepo.save(
                    Wallet.builder()
                            .account(account)
                            .balance(0)
                            .pendingBalance(0)
                            .bank(walletData.getBank())
                            .bankAccountNumber(walletData.getBankAccountNumber())
                            .cardOwner(walletData.getCardOwner())
                            .build()
            );

            Map<String, Object> data = new HashMap<>();
            data.put("pid", partner.getId());
            data.put("districtId", storeData.getDistrictId());
            data.put("wardCode", storeData.getWardCode());
            data.put("address", storeData.getAddress());
            data.put("name", storeData.getName());
            data.put("phone", storeData.getPhone());

            return ResponseBuilder.build(HttpStatus.CREATED, "", data);
        } catch (Exception e) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Something wrong", null);
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updatePartnerShippingUID(String suid, int pid) {
        Partner partner = partnerRepo.findById(pid).orElse(null);
        if(partner == null){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Partner invalid", null);
        }

        if(partnerRepo.existsByShippingUid(suid)){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "UID existed", null);
        }

        partner.setShippingUid(suid);
        partnerRepo.save(partner);
        return ResponseBuilder.build(HttpStatus.OK, "Update successfully", null);
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

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("numberSchoolAccount", schoolAccount.size());
        responseData.put("numberDesignerAccount", designerAccount.size());
        responseData.put("numberGarmentAccount", garmentAccount.size());


        return ResponseBuilder.build(HttpStatus.OK, "Number of account", responseData);
    }

    private String validateCreatePartnerAccountRequest(EncryptPartnerDataRequest.AccountData accountData) {
        if (accountData.getEmail() == null || accountData.getEmail().isEmpty() || accountRepo.existsByEmail(accountData.getEmail())) {
            return "Email invalid";
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

    @Override
    public ResponseEntity<ResponseObject> checkPartnerRegisterInfo(String email, String phone) {
        Map<String, Object> data = new HashMap<>();
        data.put("existed", accountRepo.existsByEmailOrCustomer_Phone(email, phone));
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    public ResponseEntity<ResponseObject> checkPartnerRegisterTaxCode(String taxCode) {
        Map<String, Object> data = new HashMap<>();
        data.put("existed", accountRepo.existsByCustomer_TaxCode(taxCode));
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }
}
