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

    //----------------------------ORDER----------------------------//
    @PostMapping("")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> createOrder(HttpServletRequest httpServletRequest, @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(httpServletRequest, request);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> viewOrder(HttpServletRequest request) {
        return orderService.viewSchoolOrder(request);
    }

    @PostMapping("/school/detail")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> viewSchoolOrderDetail(HttpServletRequest request, @RequestParam int orderId) {
        return orderService.viewSchoolOrderDetail(request, orderId);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> viewAllOrder() {
        return orderService.viewAllOrder();
    }

    @PostMapping("/garment")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> viewGarmentOrder(HttpServletRequest request) {
        return orderService.viewGarmentOrder(request);
    }

    @PutMapping("/cancellation")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> cancelOrder(@RequestParam(name = "orderId") int orderId) {
        return orderService.cancelOrder(orderId);
    }

    //----------------------------QUOTATION----------------------------//
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

    //----------------------------SIZE----------------------------//
    @GetMapping("/sizes")
    @PreAuthorize("hasAnyRole('SCHOOL', 'GARMENT')")
    public ResponseEntity<ResponseObject> getSizes(){
        return orderService.getSizes();
    }

    //----------------------------MILESTONE----------------------------//
    @PostMapping("/phase/create")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> createSewingPhase(HttpServletRequest httpServletRequest, @RequestBody CreateSewingPhaseRequest request) {
        return orderService.createSewingPhase(httpServletRequest, request);
    }

    @PostMapping("/phase")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> viewPhase(HttpServletRequest request) {
        return orderService.viewPhase(request);
    }

    @PostMapping("/milestone")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> assignMilestone(HttpServletRequest httpServletRequest, @RequestBody AssignMilestoneRequest request) {
        return orderService.assignMilestone(httpServletRequest, request);
    }

    @PutMapping("/milestone")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> updateMilestoneStatus(HttpServletRequest httpServletRequest, @RequestBody UpdateMilestoneStatusRequest request) {
        return orderService.updateMilestoneStatus(httpServletRequest, request);
    }

    @PostMapping("/milestone")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> viewMilestone(HttpServletRequest httpServletRequest, @RequestParam(name = "orderId") int orderId) {
        return orderService.viewMilestone(httpServletRequest, orderId);
    }


}
