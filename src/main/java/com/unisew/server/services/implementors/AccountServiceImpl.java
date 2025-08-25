package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.AccountRequest;
import com.unisew.server.models.Customer;
import com.unisew.server.models.DeactivateTicket;
import com.unisew.server.models.Partner;
import com.unisew.server.models.Wallet;
import com.unisew.server.models.WithdrawRequest;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.AccountRequestRepo;
import com.unisew.server.repositories.CustomerRepo;
import com.unisew.server.repositories.DeactivateTicketRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.WalletRepo;
import com.unisew.server.repositories.WithdrawRequestRepo;
import com.unisew.server.requests.AcceptOrRejectWithDrawRequest;
import com.unisew.server.requests.ApproveCreateAccountRequest;
import com.unisew.server.requests.ChangeAccountStatusRequest;
import com.unisew.server.requests.CreateWithDrawRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JWTService jwtService;
    private final AccountRepo accountRepo;
    private final DesignRequestRepo designRequestRepo;
    private final DeactivateTicketRepo deactivateTicketRepo;
    private final WalletRepo walletRepo;
    private final WithdrawRequestRepo withdrawRequestRepo;
    private final AccountRequestRepo accountRequestRepo;
    private final CustomerRepo customerRepo;
    private final PartnerRepo partnerRepo;

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
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Account is already " + account.getStatus().getValue(), null);
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

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createWithDrawRequest(HttpServletRequest httpServletRequest, CreateWithDrawRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account invalid", null);
        }

        Wallet wallet = walletRepo.findByAccount_Id(account.getId());

        if (wallet == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Wallet not found", null);
        }

        if (request.getWithdrawAmount() > wallet.getBalance()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Withdraw amount exceeded", null);
        }

        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .wallet(wallet)
                .creationDate(LocalDate.now())
                .withdrawAmount(request.getWithdrawAmount())
                .status(Status.WITHDRAW_PENDING)
                .build();

        withdrawRequestRepo.save(withdrawRequest);
        return ResponseBuilder.build(HttpStatus.OK, "Submit withdraw request successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllWithdraws() {

        List<WithdrawRequest> withdrawRequests = withdrawRequestRepo.findAll();
        List<Map<String, Object>> mapList = withdrawRequests.stream()
                .map(wr -> buildWithdrawResponse(wr, false))
                .toList();
        return ResponseBuilder.build(HttpStatus.OK, "Get all list withdraws successfully", mapList);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> acceptOrRejectWithDraw(AcceptOrRejectWithDrawRequest request) {

        WithdrawRequest withdrawRequest = withdrawRequestRepo.findById(request.getWithdrawId()).orElse(null);

        if (withdrawRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Withdraw not found", null);
        }

        if (withdrawRequest.getWithdrawAmount() > withdrawRequest.getWallet().getBalance()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Withdraw amount exceeded", null);
        }

        if ("approve".equalsIgnoreCase(request.getDecision())) {
            Wallet wallet = withdrawRequest.getWallet();
            wallet.setBalance(wallet.getBalance() - withdrawRequest.getWithdrawAmount());
            walletRepo.save(wallet);

            withdrawRequest.setStatus(Status.WITHDRAW_APPROVED);
            withdrawRequestRepo.save(withdrawRequest);

            return ResponseBuilder.build(HttpStatus.OK,
                    "Withdraw " + withdrawRequest.getId() + " has been approved", null);

        } else if ("reject".equalsIgnoreCase(request.getDecision())) {
            withdrawRequest.setStatus(Status.WITHDRAW_REJECTED);
            withdrawRequestRepo.save(withdrawRequest);

            return ResponseBuilder.build(HttpStatus.OK,
                    "Withdraw " + withdrawRequest.getId() + " has been rejected", null);
        }

        return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid decision", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllMyWithdraw(HttpServletRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account invalid", null);
        }

        List<WithdrawRequest> withdrawRequests = withdrawRequestRepo.findAllByWallet_Account_Id(account.getId());

        List<Map<String, Object>> mapList = withdrawRequests.stream()
                .map(wr -> buildWithdrawResponse(wr, true))
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Get my list withdraws successfully", mapList);
    }

    private Map<String, Object> buildWithdrawResponse(WithdrawRequest withdrawRequest, boolean simpleMode) {
        if (withdrawRequest == null) return null;

        List<String> keys;
        List<Object> values;

        if (simpleMode) {
            keys = List.of("id", "creationDate", "withdrawAmount", "status");
            values = List.of(
                    withdrawRequest.getId(),
                    withdrawRequest.getCreationDate(),
                    withdrawRequest.getWithdrawAmount(),
                    withdrawRequest.getStatus().getValue()
            );
        } else {
            keys = List.of("id", "creationDate", "account", "withdrawAmount", "status");
            values = List.of(
                    withdrawRequest.getId(),
                    withdrawRequest.getCreationDate(),
                    buildAccountResponse(withdrawRequest.getWallet().getAccount()),
                    withdrawRequest.getWithdrawAmount(),
                    withdrawRequest.getStatus().getValue()
            );
        }

        return MapUtils.build(keys, values);
    }

    private Map<String, Object> buildAccountResponse(Account account) {
        if (account == null) return null;

        List<String> keys = List.of(
                "id", "registerDate", "email", "role", "status"
        );
        List<Object> values = List.of(
                account.getId(), account.getRegisterDate(),
                account.getEmail(), account.getRole(), account.getStatus()
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
                "cardOwner", "bank",
                "bankAccountNumber"

        );
        List<Object> values = List.of(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getPendingBalance(),
                Objects.requireNonNullElse(wallet.getCardOwner(), ""),
                Objects.requireNonNullElse(wallet.getBank(), ""),
                Objects.requireNonNullElse(wallet.getBankAccountNumber(), "")
        );

        return MapUtils.build(keys, values);
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
    public ResponseEntity<ResponseObject> getAllAccountsRequest() {

        List<AccountRequest> accountRequests = accountRequestRepo.findAll();

        List<Map<String, Object>> mapList = accountRequests.stream()
                .map(this::buildAccountRequest)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Get list account request successfully", mapList);
    }

    private Map<String, Object> buildAccountRequest(AccountRequest accountRequest) {
        List<String> keys = List.of(
                "email", "role", "address", "taxCode", "phone", "status"
        );
        List<Object> values = List.of(
                Objects.requireNonNullElse(accountRequest.getEmail(), ""),
                Objects.requireNonNullElse(accountRequest.getRole(), ""),
                Objects.requireNonNullElse(accountRequest.getAddress(), ""),
                Objects.requireNonNullElse(accountRequest.getTaxCode(), ""),
                Objects.requireNonNullElse(accountRequest.getPhone(), ""),
                Objects.requireNonNullElse(accountRequest.getStatus(), "")
        );
        return MapUtils.build(keys, values);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> ApproveOrRejectCreateAccount(ApproveCreateAccountRequest request) {

        AccountRequest accountRequest = accountRequestRepo.findById(request.getAccountRequestId()).orElse(null);

        if (accountRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Can not find account request", null);
        }

        if (request.getStatus().equalsIgnoreCase("reject")) {

            if (!accountRequest.getStatus().getValue().equals(Status.ACCOUNT_REQUEST_PENDING.getValue())) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This account request has been processed", null);
            }

            accountRequest.setStatus(Status.ACCOUNT_REQUEST_REJECTED);
            accountRequestRepo.save(accountRequest);
            return ResponseBuilder.build(HttpStatus.OK,
                    "Account creation request rejected", null);
        }

        if (request.getStatus().equalsIgnoreCase("approve")) {

            if (!accountRequest.getStatus().getValue().equals(Status.ACCOUNT_REQUEST_PENDING.getValue())) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This account request has been processed", null);
            }

            if (accountRepo.existsByEmail(accountRequest.getEmail())) {
                return ResponseBuilder.build(HttpStatus.CONFLICT, "Email already exists", null);
            }

            accountRequest.setStatus(Status.ACCOUNT_REQUEST_APPROVED);
            accountRequestRepo.save(accountRequest);

            Account account = Account.builder()
                    .email(accountRequest.getEmail())
                    .role(accountRequest.getRole())
                    .registerDate(LocalDate.now())
                    .status(Status.ACCOUNT_ACTIVE)
                    .build();
            accountRepo.save(account);


            Wallet wallet = Wallet.builder()
                    .account(account)
                    .balance(0)
                    .pendingBalance(0)
                    .build();
            walletRepo.save(wallet);

            Customer customer = Customer.builder()
                    .account(account)
                    .address(accountRequest.getAddress())
                    .taxCode(accountRequest.getTaxCode())
                    .phone(accountRequest.getPhone())
                    .build();
            customerRepo.save(customer);


            Partner partner = Partner.builder()
                    .customer(customer)
                    .rating(0)
                    .busy(false)
                    .build();
            partnerRepo.save(partner);


            return ResponseBuilder.build(HttpStatus.CREATED, "Account created successfully", null);
        }

        return ResponseBuilder.build(HttpStatus.BAD_REQUEST,
                "Status must be either 'approve' or 'reject'", null);
    }
}