package com.unisew.server.controllers;

import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> viewAllOrder(HttpServletRequest request) {
        return orderService.viewAllOrder(request);
    }

    @PostMapping("/quotation")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, @RequestBody QuotationRequest request) {
        return orderService.createQuotation(httpServletRequest, request);
    }

    @GetMapping("/quotation")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> viewQuotation(@RequestParam(name = "orderId") int orderId) {
        return orderService.viewQuotation(orderId);
    }

    @PostMapping("/quotation/approval")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> approveQuotation(@RequestBody ApproveQuotationRequest request, HttpServletRequest httpServletRequest) {
        return orderService.approveQuotation(request,httpServletRequest);
    }

    @PutMapping("/cancellation")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> cancelOrder(@RequestParam(name = "orderId") int orderId) {
        return orderService.cancelOrder(orderId);
    }

    @GetMapping("/sizes")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getSizes(){
        return orderService.getSizes();
    }

    @PostMapping("/phase")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> createSewingPhase(HttpServletRequest httpServletRequest, @RequestBody CreateSewingPhaseRequest request) {
        return orderService.createSewingPhase(httpServletRequest, request);
    }

    @PostMapping("/milestone")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> assignMilestone(HttpServletRequest httpServletRequest, @RequestBody AssignMilestoneRequest request) {
        return orderService.assignMilestone(httpServletRequest, request);
    }

    @PutMapping("/milestone")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> updateMilestoneStatus(@RequestBody UpdateMilestoneStatusRequest request) {
        return orderService.updateMilestoneStatus(request);
    }
}
