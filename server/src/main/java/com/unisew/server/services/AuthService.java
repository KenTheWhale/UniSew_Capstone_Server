package com.unisew.server.services;

import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseObject> getGoogleUrl();

    ResponseEntity<ResponseObject> login(LoginRequest request);
}
