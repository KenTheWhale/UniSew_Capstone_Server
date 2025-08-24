package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.DeactivateTicketRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.requests.ChangeAccountStatusRequest;
import com.unisew.server.requests.UpdateCustomerBasicDataRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AccountService;
import com.unisew.server.services.JWTService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.MapUtils;
import com.unisew.server.utils.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JWTService jwtService;
    private final AccountRepo accountRepo;
    private final DesignRequestRepo designRequestRepo;
    private final DeactivateTicketRepo deactivateTicketRepo;

    @Override
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie refresh = CookieUtil.getCookie(request, "refresh");
        if (refresh == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Logout failed", null);
        }

        if (!jwtService.checkIfNotExpired(refresh.getValue())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Token invalid", null);
        }

        CookieUtil.removeCookies(response);

        return ResponseBuilder.build(HttpStatus.OK, "Logout successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateCustomerBasicData(UpdateCustomerBasicDataRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null || !account.getRole().equals(Role.SCHOOL)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid account", null);
        }

        account.getCustomer().setBusinessName(request.getBusiness());
        account.getCustomer().setAddress(request.getAddress());
        account.getCustomer().setTaxCode(request.getTaxCode());
        account.getCustomer().setPhone(request.getPhone());
        accountRepo.save(account);
        return ResponseBuilder.build(HttpStatus.OK, "Update information successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getProfile(HttpServletRequest request, String userType) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account invalid", null);
        }

        List<String> keys = List.of(
                "id", "email", "registerDate",
                "role", "status",
                "profile",
                "wallet"
        );
        List<Object> values = List.of(
                account.getId(), account.getEmail(), account.getRegisterDate(),
                account.getRole().getValue(), account.getStatus().getValue(),
                buildCustomerResponse(account.getCustomer(), userType),
                buildWalletResponse(account.getWallet())
        );
        return ResponseBuilder.build(HttpStatus.OK, "", MapUtils.build(keys, values));
    }

    @Override
    public ResponseEntity<ResponseObject> getListAccounts() {

        List<Account> accounts = accountRepo.findAll().stream()
                .filter(a -> !a.getRole().equals(Role.ADMIN))
                .toList();
        if (accounts.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "No accounts found", null);
        }
        List<Map<String, Object>> mapList = accounts.stream()
                .map(this::buildAccountResponse)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Get list account success", mapList);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> changeAccountStatus(ChangeAccountStatusRequest request) {

        Account account = accountRepo.findById(request.getAccountId()).orElse(null);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }

        if (account.getStatus().getValue().equalsIgnoreCase(request.getStatus())) {
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Account is already " +  account.getStatus().getValue(), null);
        }

        account.setStatus(Status.valueOf(request.getStatus()));
        accountRepo.save(account);

        DeactivateTicket deactivateTicket = DeactivateTicket.builder()
                .account(account)
                .reason(request.getReason())
                .startDate(LocalDate.now())
                .endDate(request.getEndDate())
                .build();
        deactivateTicketRepo.save(deactivateTicket);

        return ResponseBuilder.build(HttpStatus.OK, "Change account status successfully", null);
    }

    private Map<String, Object> buildAccountResponse(Account account) {
        if (account == null) return null;

        List<String> keys = List.of(
                "id", "registerDate", "email", "role", "status"
        );
        List<Object> values = List.of(
                account.getId(), account.getRegisterDate(),
                account.getEmail(),account.getRole(),account.getStatus()
        );
        return MapUtils.build(keys, values);
    }

    private Map<String, Object> buildCustomerResponse(Customer customer, String userType) {
        if (customer == null) return null;
        List<String> keys = List.of(
                "address", "avatar", "businessName",
                "name", "phone", "taxCode"
        );
        List<Object> values = List.of(
                customer.getAddress(), customer.getAvatar(), customer.getBusinessName(),
                customer.getName(), customer.getPhone(), customer.getTaxCode()
        );

        if (!userType.equalsIgnoreCase(Role.SCHOOL.getValue())) {
            keys = List.of(
                    "address", "avatar", "businessName",
                    "name", "phone", "taxCode",
                    "partner"
            );

            values = List.of(
                    customer.getAddress(), customer.getAvatar(), customer.getBusinessName(),
                    customer.getName(), customer.getPhone(), customer.getTaxCode(),
                    buildPartnerResponse(customer.getPartner())
            );
        }

        return MapUtils.build(keys, values);
    }

    private Map<String, Object> buildPartnerResponse(Partner partner) {
        if (partner == null) return null;
        List<String> keys = List.of(
                "busy", "endTime", "startTime",
                "preview", "rating"
        );

        List<Object> values = List.of(
                partner.isBusy(), partner.getEndTime(), partner.getStartTime(),
                partner.getInsidePreview(), partner.getRating()
        );

        return MapUtils.build(keys, values);
    }

    private Map<String, Object> buildWalletResponse(Wallet wallet) {
        if (wallet == null) return null;
        List<String> keys = List.of(
                "id", "balance", "pendingBalance",
                "cardExpiration", "cardName",
                "cardNumber"

        );
        List<Object> values = List.of(
                wallet.getId(), wallet.getBalance(), wallet.getPendingBalance(),
                wallet.getCardExpiredDate(), wallet.getCardName(),
                wallet.getCardNumber()
        );

        return MapUtils.build(keys, values);
    }
}