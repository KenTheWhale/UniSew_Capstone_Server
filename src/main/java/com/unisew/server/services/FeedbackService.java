package com.unisew.server.services;

import com.unisew.server.requests.ApproveReportRequest;
import com.unisew.server.requests.GiveFeedbackRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface FeedbackService {

    ResponseEntity<ResponseObject> getFeedbacksByOrder(Integer orderId);

    ResponseEntity<ResponseObject> giveFeedback(HttpServletRequest httpServletRequest, GiveFeedbackRequest request);

    ResponseEntity<ResponseObject> getFeedbacksByDesign(Integer designRequestId);

    ResponseEntity<ResponseObject> getFeedbackByGarment(HttpServletRequest httpServletRequest);

    ResponseEntity<ResponseObject> getFeedbackByDesigner(HttpServletRequest httpServletRequest);

    ResponseEntity<ResponseObject> approveReport(ApproveReportRequest request);

    ResponseEntity<ResponseObject> getAllReport();
}
