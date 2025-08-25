package com.unisew.server.validations;

import com.unisew.server.models.Feedback;
import com.unisew.server.requests.ApproveReportRequest;
import com.unisew.server.requests.GiveFeedbackRequest;

import java.util.Objects;

public class FeedbackValidation {
    public static String validateGiveFeedback(GiveFeedbackRequest request) {
        if (request == null) return "Request body is required";

        boolean hasRequestId = request.getRequestId() != null;
        boolean hasOrderId = request.getOrderId() != null;

        if (!hasRequestId && !hasOrderId) {
            return "Exactly one of requestId or orderId must be provided";
        }

        if (request.isReport()) {
            if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
                return "Rating must be between 1 and 5";
            }
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return "Content is required";
        }

        if (request.getImageUrls() != null) {
            boolean hasInvalid = request.getImageUrls().stream().filter(Objects::nonNull).map(String::trim).anyMatch(String::isEmpty);
            if (hasInvalid) return "Images are required";
            if (request.getImageUrls().size() > 3) return "Maximum 3 images are allowed";
        }
        return null;
    }

    public static String validateApproveReport(ApproveReportRequest request, Feedback report) {
        if (request == null) return "Request is null";
        if (report == null) return "Feedback not found";
        if (!report.isReport()) return "Feedback is not marked as report";
        if (request.getMessageForSchool() != null && request.getMessageForSchool().length() > 2000) {
            return "Message for school is too long";
        }
        if (request.getMessageForPartner() != null && request.getMessageForPartner().length() > 2000) {
            return "Message for partner is too long";
        }
        return null;
    }
}
