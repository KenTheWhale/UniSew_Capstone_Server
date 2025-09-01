package com.unisew.server.controllers;

import com.unisew.server.requests.AppealReportRequest;
import com.unisew.server.requests.ApproveAppealRequest;
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

    @PostMapping("/garment")
    @PreAuthorize("hasAnyRole('ADMIN', 'GARMENT')")
    public ResponseEntity<ResponseObject> getFeedbackByGarment(HttpServletRequest httpServletRequest) {
        return feedbackService.getFeedbackByGarment(httpServletRequest);
    }

    @PostMapping("/designer")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<ResponseObject> getFeedbackByDesigner(HttpServletRequest httpServletRequest) {
        return feedbackService.getFeedbackByDesigner(httpServletRequest);
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

    @PostMapping("/appeal")
    @PreAuthorize("hasAnyRole('SCHOOL', 'DESINGER', 'GARMENT')")
    public ResponseEntity<ResponseObject> appealReport(@RequestBody AppealReportRequest request, HttpServletRequest  httpServletRequest) {
        return feedbackService.appealReport(request, httpServletRequest);
    }

    @PostMapping("/appeal")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject> approveAppeal(@RequestBody ApproveAppealRequest request) {
        return feedbackService.approveAppeal(request);
    }

}
