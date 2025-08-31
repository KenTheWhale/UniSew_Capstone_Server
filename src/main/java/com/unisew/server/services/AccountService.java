package com.unisew.server.services;

import com.unisew.server.requests.AcceptOrRejectWithDrawRequest;
import com.unisew.server.requests.ApproveCreateAccountRequest;
import com.unisew.server.requests.ChangeAccountStatusRequest;
import com.unisew.server.requests.CheckSchoolInitRequest;
import com.unisew.server.requests.CreateWithDrawRequest;
import com.unisew.server.requests.UpdateCustomerBasicDataRequest;
import com.unisew.server.requests.UpdatePartnerProfileRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AccountService {

    ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ResponseObject> updateCustomerBasicData(UpdateCustomerBasicDataRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getProfile(HttpServletRequest request, String userType);

    ResponseEntity<ResponseObject> getListAccounts();

    ResponseEntity<ResponseObject> changeAccountStatus(ChangeAccountStatusRequest request);

    ResponseEntity<ResponseObject> createWithDrawRequest(HttpServletRequest httpServletRequest, CreateWithDrawRequest request);

    ResponseEntity<ResponseObject> getAllWithdraws();

    ResponseEntity<ResponseObject> acceptOrRejectWithDraw(AcceptOrRejectWithDrawRequest request);

    ResponseEntity<ResponseObject> getAllMyWithdraw(HttpServletRequest request);

    ResponseEntity<ResponseObject> getAccessToken(HttpServletRequest request);

    ResponseEntity<ResponseObject> updatePartnerProfile(HttpServletRequest request, UpdatePartnerProfileRequest updatePartnerProfileRequest);

    ResponseEntity<ResponseObject> checkSchoolInit(CheckSchoolInitRequest request);
}