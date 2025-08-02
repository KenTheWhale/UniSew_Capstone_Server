package com.unisew.server.services;

import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AccountService {

    ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response);
}
