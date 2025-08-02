package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Customer;
import com.unisew.server.models.Partner;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.CustomerRepo;
import com.unisew.server.repositories.WalletRepo;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    @Value("${jwt.expiration.access-token}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshExpiration;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AccountRepo accountRepo;

    private final CustomerRepo customerRepo;

    private final WalletRepo walletRepo;

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request, HttpServletResponse response) {
        Account account = accountRepo.findByEmail(request.getEmail()).orElse(null);
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
}
