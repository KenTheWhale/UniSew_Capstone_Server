package com.unisew.server.services.implementors;

import com.unisew.server.enums.PaymentType;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AccountService;
import com.unisew.server.services.JWTService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
import com.unisew.server.utils.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JWTService jwtService;
    private final AccountRepo accountRepo;
    private final DesignRequestRepo designRequestRepo;
    private final DeactivateTicketRepo deactivateTicketRepo;
    private final WalletRepo walletRepo;
    private final WithdrawRequestRepo withdrawRequestRepo;
    private final CustomerRepo customerRepo;
    private final PartnerRepo partnerRepo;
    private final DesignQuotationRepo designQuotationRepo;
    private final TransactionRepo transactionRepo;

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

        Map<String, Object> data = new HashMap<>();

        data.put("id", account.getId());
        data.put("email", account.getEmail());
        data.put("registerDate", account.getRegisterDate());
        data.put("role", account.getRole().getValue());
        data.put("status", account.getStatus().getValue());
        data.put("profile", buildCustomerResponse(account.getCustomer(), userType));
        data.put("wallet", buildWalletResponse(account.getWallet()));

        return ResponseBuilder.build(HttpStatus.OK, "", data);
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
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Account is already " + account.getStatus().getValue(), null);
        }

        account.setStatus(Status.valueOf(request.getStatus()));
        accountRepo.save(account);

        DeactivateTicket deactivateTicket = DeactivateTicket.builder()
                .account(account)
                .reason(request.getReason())
                .startDate(LocalDateTime.now())
                .endDate(request.getEndDate())
                .build();
        deactivateTicketRepo.save(deactivateTicket);

        return ResponseBuilder.build(HttpStatus.OK, "Change account status successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createWithDrawRequest(HttpServletRequest httpServletRequest, CreateWithDrawRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account invalid", null);
        }

        Wallet wallet = account.getWallet();

        if (request.getWithdrawAmount() > wallet.getBalance()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Withdraw amount exceeded", null);
        }

        withdrawRequestRepo.save(WithdrawRequest.builder()
                .wallet(wallet)
                .creationDate(LocalDateTime.now())
                .withdrawAmount(request.getWithdrawAmount())
                .status(Status.WITHDRAW_PENDING)
                .build());

        return ResponseBuilder.build(HttpStatus.OK, "Submit withdraw request successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllWithdraws() {
        List<Map<String, Object>> mapList = withdrawRequestRepo.findAll().stream()
                .map(this::buildWithdrawResponse)
                .toList();
        return ResponseBuilder.build(HttpStatus.OK, "Get all list withdraws successfully", mapList);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> processWithdraw(ProcessWithdrawRequest request) {

        WithdrawRequest withdrawRequest = withdrawRequestRepo.findById(request.getWithdrawId()).orElse(null);

        if (withdrawRequest == null || !withdrawRequest.getStatus().equals(Status.WITHDRAW_PENDING)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Withdraw request invalid", null);
        }

        if (withdrawRequest.getWithdrawAmount() > withdrawRequest.getWallet().getBalance()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Withdraw amount exceeded", null);
        }

        if (request.isApproved()) {
            Wallet wallet = withdrawRequest.getWallet();
            long newBalance = wallet.getBalance() - withdrawRequest.getWithdrawAmount();
            wallet.setBalance(newBalance);
            wallet = walletRepo.save(wallet);

            withdrawRequest.setStatus(Status.WITHDRAW_APPROVED);
            withdrawRequest.setEvidenceImageUrl(request.getEvidenceImage());
            withdrawRequestRepo.save(withdrawRequest);

            Map<String, Object> remainingBalance = new HashMap<>();
            remainingBalance.put("sender", 0L);
            remainingBalance.put("receiver", newBalance);

            transactionRepo.save(
                    Transaction.builder()
                            .wallet(wallet)
                            .receiver(wallet.getAccount().getCustomer())
                            .sender(wallet.getAccount().getCustomer())
                            .itemId(0)
                            .receiverName(wallet.getAccount().getCustomer().getName())
                            .senderName(wallet.getAccount().getCustomer().getName())
                            .amount(withdrawRequest.getWithdrawAmount())
                            .paymentType(PaymentType.WITHDRAW)
                            .serviceFee(0)
                            .status(Status.TRANSACTION_SUCCESS)
                            .creationDate(LocalDateTime.now())
                            .balanceType("balance")
                            .paymentGatewayCode("00")
                            .remainingBalance(remainingBalance)
                            .build()
            );

            return ResponseBuilder.build(HttpStatus.OK, "Withdraw request has been approved", null);

        }

        withdrawRequest.setStatus(Status.WITHDRAW_REJECTED);
        withdrawRequestRepo.save(withdrawRequest);

        return ResponseBuilder.build(HttpStatus.OK, "Withdraw request has been rejected", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllMyWithdraws(HttpServletRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account invalid", null);
        }

        List<Map<String, Object>> mapList = account.getWallet().getWithdrawRequests().stream()
                .map(this::buildWithdrawResponse)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Get my list withdraws successfully", mapList);
    }

    private Map<String, Object> buildWithdrawResponse(WithdrawRequest withdrawRequest) {
        if (withdrawRequest == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", withdrawRequest.getId());
        data.put("creationDate", withdrawRequest.getCreationDate());
        data.put("withdrawAmount", withdrawRequest.getWithdrawAmount());
        data.put("status", withdrawRequest.getStatus().getValue());

        return data;
    }

    private Map<String, Object> buildAccountResponse(Account account) {
        if (account == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", account.getId());
        data.put("registerDate", account.getRegisterDate());
        data.put("name", account.getCustomer().getName());
        data.put("email", account.getEmail());
        data.put("role", account.getRole());
        data.put("status", account.getStatus());
        data.put("code", account.getWallet().getBank());
        data.put("accountNo", account.getWallet().getBankAccountNumber());

        return data;
    }

    private Map<String, Object> buildCustomerResponse(Customer customer, String userType) {
        if (customer == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("address", customer.getAddress());
        data.put("avatar", customer.getAvatar());
        data.put("businessName", customer.getBusinessName());
        data.put("name", customer.getName());
        data.put("phone", customer.getPhone());
        data.put("taxCode", customer.getTaxCode());

        if (!userType.equalsIgnoreCase(Role.SCHOOL.getValue())) {
            data.put("partner", buildPartnerResponse(customer.getPartner()));
        }

        return data;
    }

    private Map<String, Object> buildPartnerResponse(Partner partner) {
        if (partner == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("endTime", partner.getEndTime());
        data.put("startTime", partner.getStartTime());
        data.put("preview", partner.getInsidePreview());
        data.put("rating", partner.getRating());

        return data;
    }

    private Map<String, Object> buildWalletResponse(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", wallet.getId());
        data.put("balance", wallet.getBalance());
        data.put("pendingBalance", wallet.getPendingBalance());
        data.put("cardOwner", Objects.requireNonNullElse(wallet.getCardOwner(), ""));
        data.put("bank", Objects.requireNonNullElse(wallet.getBank(), ""));
        data.put("bankAccountNumber", Objects.requireNonNullElse(wallet.getBankAccountNumber(), ""));

        return data;
    }

    @Override
    public ResponseEntity<ResponseObject> getAccessToken(HttpServletRequest request) {
        Cookie access = CookieUtil.getCookie(request, "access");
        if (access == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No access", null);
        }

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No account", null);
        }


        Map<String, Object> data = new HashMap<>();
        data.put("access", access.getValue());
        data.put("id", account.getId());
        data.put("email", account.getEmail());
        data.put("role", account.getRole().getValue());
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updatePartnerProfile(HttpServletRequest request, UpdatePartnerProfileRequest updatePartnerProfileRequest) {

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid account", null);
        }
        Wallet wallet = account.getWallet();
        Customer customer = account.getCustomer();
        Partner partner = account.getCustomer().getPartner();
        if (wallet == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Can not find wallet", null);
        }
        if (partner == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Can not find partner", null);
        }

        UpdatePartnerProfileRequest.WalletDTO walletRequest = updatePartnerProfileRequest.getWallet();
        UpdatePartnerProfileRequest.CustomerDTO customerRequest = updatePartnerProfileRequest.getCustomer();
        UpdatePartnerProfileRequest.PartnerDTO partnerRequest = updatePartnerProfileRequest.getPartner();

        wallet.setCardOwner(walletRequest.getOwnerName());
        wallet.setBank(walletRequest.getBank());
        wallet.setBankAccountNumber(walletRequest.getBankAccountNumber());
        walletRepo.save(wallet);

        customer.setName(customerRequest.getName());
        customer.setBusinessName(customerRequest.getBusinessName());
        customer.setAvatar(customerRequest.getAvatarUrl());
        customerRepo.save(customer);

        partner.setStartTime(partnerRequest.getStartTime());
        partner.setEndTime(partnerRequest.getEndTime());
        partnerRepo.save(partner);

        return ResponseBuilder.build(HttpStatus.OK, "Partner profile updated successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> checkSchoolInit(CheckSchoolInitRequest request) {
        Customer school = customerRepo.findByBusinessName(request.getSchoolName()).orElse(null);
        if (request.getStep() == 1) {
            if (school == null) {
                if (customerRepo.existsByAddress(request.getAddress())) {
                    return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Address is used", null);
                }

                return ResponseBuilder.build(HttpStatus.OK, "", null);
            }

            if (request.getAddress().equalsIgnoreCase(school.getAddress())) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This school is registered", null);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("taxCode", school.getTaxCode().equals("N/A") ? "" : school.getTaxCode());
            data.put("phone", school.getPhone().equals("N/A") ? "" : school.getPhone());
            return ResponseBuilder.build(HttpStatus.OK, "", data);
        }

        if (school == null) {
            if (customerRepo.existsByTaxCode(request.getTaxCode())) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Tax code is used", null);
            }

            if (customerRepo.existsByPhone(request.getPhone())) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Phone is used", null);
            }

            return ResponseBuilder.build(HttpStatus.OK, "", null);
        }
        return ResponseBuilder.build(HttpStatus.OK, "", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getProfilePartner(GetProfilePartnerRequest request) {
        Partner partner = partnerRepo.findById(request.getPartnerId()).orElse(null);
        if (partner == null) {
            return ResponseBuilder.build(HttpStatus.UNAUTHORIZED, "Unauthorized", null);
        }

        Map<String, Object> data = EntityResponseBuilder.buildPartnerResponse(partner, designQuotationRepo, designRequestRepo);


        return ResponseBuilder.build(HttpStatus.OK, "Get profile success", data);
    }
}