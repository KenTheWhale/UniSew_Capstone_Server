package com.unisew.server.controllers;

import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/google/url")
    public ResponseEntity<ResponseObject> getGoogleUrl(){
        return authService.getGoogleUrl();
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}
