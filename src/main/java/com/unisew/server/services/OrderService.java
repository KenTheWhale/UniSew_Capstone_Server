package com.unisew.server.services;

import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {


    ResponseEntity<ResponseObject> createOrder(HttpServletRequest httpServletRequest, CreateOrderRequest request);

    ResponseEntity<ResponseObject> viewSchoolOrder(HttpServletRequest request);

    ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, QuotationRequest request);

    ResponseEntity<ResponseObject> approveQuotation(ApproveQuotationRequest request, HttpServletRequest httpServletRequest);

    ResponseEntity<ResponseObject> viewQuotation(int orderId);

    ResponseEntity<ResponseObject> getSizes();

    ResponseEntity<ResponseObject> viewAllOrder();

    ResponseEntity<ResponseObject> cancelOrder(int orderId);

    ResponseEntity<ResponseObject> createSewingPhase(HttpServletRequest httpServletRequest, CreateSewingPhaseRequest request);

    ResponseEntity<ResponseObject> assignMilestone(HttpServletRequest httpServletRequest, AssignMilestoneRequest request);

    ResponseEntity<ResponseObject> updateMilestoneStatus(HttpServletRequest httpServletRequest, UpdateMilestoneStatusRequest request);

    ResponseEntity<ResponseObject> viewMilestone(HttpServletRequest httpServletRequest, int orderId);

    ResponseEntity<ResponseObject> viewPhase(HttpServletRequest request);

    ResponseEntity<ResponseObject> viewGarmentOrder(HttpServletRequest request);

    ResponseEntity<ResponseObject> viewSchoolOrderDetail(HttpServletRequest request, int orderId);
}
