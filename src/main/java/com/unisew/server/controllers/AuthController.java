package com.unisew.server.controllers;

import com.unisew.server.requests.CreatePartnerAccountRequest;
import com.unisew.server.requests.EncryptPartnerDataRequest;
import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @GetMapping("/partner/info")
    public ResponseEntity<ResponseObject> checkPartnerRegisterInfo(@RequestParam String email, @RequestParam String phone){
        return authService.checkPartnerRegisterInfo(email, phone);
    }

    @GetMapping("/partner/tax")
    public ResponseEntity<ResponseObject> checkPartnerRegisterTaxCode(@RequestParam String taxCode){
        return authService.checkPartnerRegisterTaxCode(taxCode);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response) {
        return authService.refresh(request, response);
    }

    @PostMapping("/partner/data/encrypt")
    public ResponseEntity<ResponseObject> encryptPartnerData(@RequestBody EncryptPartnerDataRequest request) {
        return authService.encryptPartnerData(request);
    }

    @PostMapping("/partner/register")
    public ResponseEntity<ResponseObject> createPartnerAccount(@RequestBody CreatePartnerAccountRequest request) {
        return authService.createPartnerAccount(request);
    }

    @GetMapping("/partner/suid")
    public ResponseEntity<ResponseObject> updatePartnerShippingUID(@RequestParam String suid, @RequestParam int pid) {
        return authService.updatePartnerShippingUID(suid, pid);
    }

    @GetMapping("/number")
    public ResponseEntity<ResponseObject> getNumberAccountRole() {
        return authService.getNumberAccountRole();
    }
}
