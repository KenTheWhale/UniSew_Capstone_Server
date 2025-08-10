package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.models.Account;
import com.unisew.server.models.Customer;
import com.unisew.server.models.Partner;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.AccountRepo;
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

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JWTService jwtService;
    private final AccountRepo accountRepo;

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

        if(!userType.equalsIgnoreCase(Role.SCHOOL.getValue())){
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

    private Map<String, Object> buildPartnerResponse(Partner partner){
        if(partner == null) return null;
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

    private Map<String, Object> buildWalletResponse(Wallet wallet){
        if(wallet == null) return null;
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
