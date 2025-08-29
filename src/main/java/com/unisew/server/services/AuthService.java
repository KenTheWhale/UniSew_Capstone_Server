package com.unisew.server.services;

import com.unisew.server.requests.CreatePartnerAccountRequest;
import com.unisew.server.requests.EncryptPartnerDataRequest;
import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseObject> login(LoginRequest request, HttpServletResponse response);

    ResponseEntity<ResponseObject> refresh(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ResponseObject> encryptPartnerData(EncryptPartnerDataRequest request);

    ResponseEntity<ResponseObject> createPartnerAccountRequest(CreatePartnerAccountRequest request);

    ResponseEntity<ResponseObject> getNumberAccountRole();

    ResponseEntity<ResponseObject> checkPartnerRegisterInfo(String email, String phone);

    ResponseEntity<ResponseObject> updatePartnerShippingUID(String suid, int pid);

    ResponseEntity<ResponseObject> checkPartnerRegisterTaxCode(String taxCode);
}
