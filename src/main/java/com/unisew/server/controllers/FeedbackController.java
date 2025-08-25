package com.unisew.server.controllers;

import com.unisew.server.requests.ApproveReportRequest;
import com.unisew.server.requests.GiveFeedbackRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
@Tag(name = "Feedback", description = "Feedback ik")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/order")
    public ResponseEntity<ResponseObject> getFeedbacksByOrder(@RequestParam(name = "orderId") Integer orderId) {
        return feedbackService.getFeedbacksByOrder(orderId);
    }

    @GetMapping("/design")
    public ResponseEntity<ResponseObject> getFeedbacksByDesign(@RequestParam(name = "designRequestId") Integer designRequestId) {
        return feedbackService.getFeedbacksByDesign(designRequestId);
    }

    @GetMapping("/garment")
    @PreAuthorize("hasAnyRole('ADMIN', 'GARMENT')")
    public ResponseEntity<ResponseObject> getFeedbackByGarment(@RequestParam(name = "garmentId") Integer garmentId) {
        return feedbackService.getFeedbackByGarment(garmentId);
    }

    @GetMapping("/designer")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<ResponseObject> getFeedbackByDesigner(@RequestParam(name = "designerId") Integer designerId) {
        return feedbackService.getFeedbackByDesigner(designerId);
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllReport() {
        return feedbackService.getAllReport();
    }

    @PostMapping("")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> giveFeedback(HttpServletRequest httpServletRequest, @RequestBody GiveFeedbackRequest request) {
        return feedbackService.giveFeedback(httpServletRequest, request);
    }

    @PostMapping("/approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> approveReport(@RequestBody ApproveReportRequest request) {
        return feedbackService.approveReport(request);
    }
}
