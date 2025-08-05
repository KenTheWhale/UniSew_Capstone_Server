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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    @PreAuthorize("hasAuthority('GARMENT')")
    public ResponseEntity<ResponseObject> createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('GARMENT')")
    public ResponseEntity<ResponseObject> viewOrder() {
        return orderService.viewOrder();
    }

    @PostMapping("/quotation")
    @PreAuthorize("hasAuthority('GARMENT')")
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, @RequestBody QuotationRequest request) {
        return orderService.createQuotation(httpServletRequest, request);
    }

    @GetMapping("/quotation/{orderId}")
    @PreAuthorize("hasAuthority('GARMENT')")
    public ResponseEntity<ResponseObject> viewQuotation(@PathVariable(name = "orderId") int orderId) {
        return orderService.viewQuotation(orderId);
    }

    @GetMapping("/quotation/approval/{quotationId}")
    @PreAuthorize("hasAuthority('GARMENT')")
    public ResponseEntity<ResponseObject> approveQuotation(@PathVariable(name = "quotationId") int quotationId) {
        return orderService.approveQuotation(quotationId);
    }


}
