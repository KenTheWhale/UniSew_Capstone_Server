package com.unisew.server.services;

import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.QuotationRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {


    ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request);

    ResponseEntity<ResponseObject> viewOrder();

    ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, QuotationRequest request);
}
