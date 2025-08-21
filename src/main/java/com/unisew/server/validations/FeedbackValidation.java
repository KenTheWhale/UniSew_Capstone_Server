package com.unisew.server.validations;

import com.unisew.server.models.Feedback;
import com.unisew.server.requests.ApproveReportRequest;
import com.unisew.server.requests.GiveFeedbackRequest;

public class FeedbackValidation {
    public static String validateGiveFeedback(GiveFeedbackRequest request) {
        if (request == null) return "Request body is required";

        boolean hasRequestId = request.getRequestId() != null;
        boolean hasOrderId = request.getOrderId() != null;

        if (!hasRequestId || !hasOrderId) {
            return "Exactly one of requestId or orderId must be provided";
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            return "Rating must be between 1 and 5";
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return "Content is required";
        }

        return null;
    }

    public static String validateApproveFeedback(ApproveReportRequest request, Feedback report) {
        if (request == null) return "Request is null";
        if (report == null) return "Feedback not found";
        if (!report.isReport()) return "Feedback is not marked as report";
        if (request.getAdminMessage() != null && request.getAdminMessage().length() > 2000) {
            return "Admin message is too long";
        }
        return null;
    }
}
