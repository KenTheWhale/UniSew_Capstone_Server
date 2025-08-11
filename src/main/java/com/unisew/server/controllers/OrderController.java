package com.unisew.server.controllers;

import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.QuotationRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.OrderService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Tag(name = "Order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> viewOrder(HttpServletRequest request) {
        return orderService.viewSchoolOrder(request);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> viewAllOrder(HttpServletRequest request) {
        return orderService.viewAllOrder(request);
    }

    @PostMapping("/quotation")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, @RequestBody QuotationRequest request) {
        return orderService.createQuotation(httpServletRequest, request);
    }

    @GetMapping("/quotation/{orderId}")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> viewQuotation(@PathVariable(name = "orderId") int orderId) {
        return orderService.viewQuotation(orderId);
    }

    @GetMapping("/quotation/approval/{quotationId}")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> approveQuotation(@PathVariable(name = "quotationId") int quotationId) {
        return orderService.approveQuotation(quotationId);
    }


}
