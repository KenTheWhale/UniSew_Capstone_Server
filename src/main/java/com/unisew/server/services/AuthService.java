package com.unisew.server.services;

import com.unisew.server.requests.CreatePartnerAccountRequestRequest;
import com.unisew.server.requests.EncryptPartnerDataRequest;
import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthService {
    ResponseEntity<ResponseObject> login(LoginRequest request, HttpServletResponse response);

    ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ResponseObject> encryptPartnerData(EncryptPartnerDataRequest request);

    ResponseEntity<ResponseObject> createPartnerAccountRequest(CreatePartnerAccountRequestRequest request);

    ResponseEntity<ResponseObject> getNumberAccountRole();
}
