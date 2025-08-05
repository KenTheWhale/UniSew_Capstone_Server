package com.unisew.server.controllers;

import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.QuotationRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.OrderService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> viewOrder() {
        return orderService.viewOrder();
    }

    @PostMapping("/quotation")
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, @RequestBody QuotationRequest request) {
        return orderService.createQuotation(httpServletRequest, request);
    }



}
