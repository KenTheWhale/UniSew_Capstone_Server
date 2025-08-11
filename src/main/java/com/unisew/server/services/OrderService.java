package com.unisew.server.services;

import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.QuotationRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {


    ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request);

    ResponseEntity<ResponseObject> viewSchoolOrder(HttpServletRequest request);

    ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, QuotationRequest request);

    ResponseEntity<ResponseObject> approveQuotation(int quotationId);

    ResponseEntity<ResponseObject> viewQuotation(int orderId);

    ResponseEntity<ResponseObject> getSizes();

    ResponseEntity<ResponseObject> viewAllOrder(HttpServletRequest request);
}
