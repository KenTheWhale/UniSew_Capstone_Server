package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.models.Account;
import com.unisew.server.models.Customer;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.requests.UpdateCustomerBasicDataRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AccountService;
import com.unisew.server.services.JWTService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if(account == null || !account.getRole().equals(Role.SCHOOL)){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid account", null);
        }

        account.getCustomer().setBusinessName(request.getBusiness());
        account.getCustomer().setAddress(request.getAddress());
        account.getCustomer().setTaxCode(request.getTaxCode());
        account.getCustomer().setPhone(request.getPhone());
        accountRepo.save(account);
        return ResponseBuilder.build(HttpStatus.OK, "Update information successfully", null);
    }
}
