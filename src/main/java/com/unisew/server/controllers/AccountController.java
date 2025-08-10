package com.unisew.server.controllers;

import com.unisew.server.requests.UpdateCustomerBasicDataRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'SCHOOL', 'GARMENT')")
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response) {
        return accountService.logout(request, response);
    }

    @PutMapping("/data/customer")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> updateCustomerBasicData(@RequestBody UpdateCustomerBasicDataRequest request, HttpServletRequest httpRequest) {
        return accountService.updateCustomerBasicData(request, httpRequest);
    }

    @PostMapping("/profile/school")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getSchoolProfile(HttpServletRequest request){
        return accountService.getProfile(request, "school");
    }

    @PostMapping("/profile/partner")
    @PreAuthorize("hasAnyRole('DESIGNER', 'GARMENT')")
    public ResponseEntity<ResponseObject> getPartnerProfile(HttpServletRequest request){
        return accountService.getProfile(request, "partner");
    }
}
