package com.unisew.server.controllers;

import com.unisew.server.models.WithdrawRequest;
import com.unisew.server.requests.*;
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

    @PostMapping("/access")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'SCHOOL', 'GARMENT')")
    public ResponseEntity<ResponseObject> getAccessToken(HttpServletRequest request){
        return accountService.getAccessToken(request);
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
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAccountList() {
        return accountService.getListAccounts();
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> changeAccountStatus(@RequestBody ChangeAccountStatusRequest request) {
        return accountService.changeAccountStatus(request);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('DESIGNER', 'SCHOOL', 'GARMENT')")
    public ResponseEntity<ResponseObject> withdraw(HttpServletRequest httpRequest, @RequestBody CreateWithDrawRequest request ) {
        return accountService.createWithDrawRequest(httpRequest, request);
    }
    @GetMapping("/withdraw/all-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getWithdrawList() {
        return accountService.getAllWithdraws();
    }

    @PutMapping("/withdraw/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> withdrawDecision(@RequestBody AcceptOrRejectWithDrawRequest request) {
        return accountService.acceptOrRejectWithDraw(request);
    }

    @PostMapping("/withdraw/my-list")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'SCHOOL', 'GARMENT')")
    public ResponseEntity<ResponseObject> getAllMyWithdraw(HttpServletRequest request) {
        return accountService.getAllMyWithdraw(request);
    }

    @GetMapping("/account-request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllAccountRequest() {
        return accountService.getAllAccountsRequest();
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> ApproveCreateAccount(@RequestBody ApproveCreateAccountRequest request) {
        return accountService.approveCreateAccount(request);
    }
}