package com.unisew.server.controllers;

import com.unisew.server.requests.CreatePartnerAccountRequestRequest;
import com.unisew.server.requests.EncryptPartnerDataRequest;
import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response) {
        return authService.refresh(request, response);
    }

    @PostMapping("/partner/data/encrypt")
    public ResponseEntity<ResponseObject> encryptPartnerData(@RequestBody EncryptPartnerDataRequest request){
        return authService.encryptPartnerData(request);
    }

    @PostMapping("/partner/register")
    public ResponseEntity<ResponseObject> createPartnerAccountRequest(@RequestBody CreatePartnerAccountRequestRequest request){
        return authService.createPartnerAccountRequest(request);
    }
}
