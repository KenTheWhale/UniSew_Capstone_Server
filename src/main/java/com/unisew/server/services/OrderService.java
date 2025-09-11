package com.unisew.server.services;

import com.unisew.server.controllers.ConfirmOrderRequest;
import com.unisew.server.requests.ApproveQuotationRequest;
import com.unisew.server.requests.AssignMilestoneRequest;
import com.unisew.server.requests.CancelOrderRequest;
import com.unisew.server.requests.ConfirmDeliveredOrderRequest;
import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.CreateSewingPhaseRequest;
import com.unisew.server.requests.DeleteSewingPhaseRequest;
import com.unisew.server.requests.QuotationRequest;
import com.unisew.server.requests.UpdateMilestoneStatusRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {


    ResponseEntity<ResponseObject> createOrder(HttpServletRequest httpServletRequest, CreateOrderRequest request);

    ResponseEntity<ResponseObject> viewSchoolOrder(HttpServletRequest request);

    ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, QuotationRequest request);

    ResponseEntity<ResponseObject> approveQuotation(ApproveQuotationRequest request, HttpServletRequest httpServletRequest);

    ResponseEntity<ResponseObject> viewQuotation(int orderId);

    ResponseEntity<ResponseObject> getSizes();

    ResponseEntity<ResponseObject> viewAllOrder();

    ResponseEntity<ResponseObject> cancelOrder(CancelOrderRequest request);

    ResponseEntity<ResponseObject> createSewingPhase(HttpServletRequest httpServletRequest, CreateSewingPhaseRequest request);

    ResponseEntity<ResponseObject> assignMilestone(HttpServletRequest httpServletRequest, AssignMilestoneRequest request);

    ResponseEntity<ResponseObject> updateMilestoneStatus(HttpServletRequest httpServletRequest, UpdateMilestoneStatusRequest request);

    ResponseEntity<ResponseObject> viewMilestone(HttpServletRequest httpServletRequest, int orderId);

    ResponseEntity<ResponseObject> viewPhase(HttpServletRequest request);

    ResponseEntity<ResponseObject> viewGarmentOrder(HttpServletRequest request);

    ResponseEntity<ResponseObject> viewSchoolOrderDetail(HttpServletRequest request, int orderId);

    ResponseEntity<ResponseObject> deleteSewingPhase(int sewingPhaseId, HttpServletRequest httpServletRequest);

    ResponseEntity<ResponseObject> confirmDeliveredOrder(ConfirmDeliveredOrderRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> confirmOrder(ConfirmOrderRequest request);

    ResponseEntity<ResponseObject> viewAllOrderAdmin();
}
