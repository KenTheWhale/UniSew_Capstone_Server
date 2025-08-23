package com.unisew.server.validations;

import com.unisew.server.requests.QuotationRequest;

import java.time.LocalDate;

public class QuotationValidation {
    public static String validate(QuotationRequest request) {
        if (request == null) {
            return "Invalid request!";
        }
        if (request.getOrderId() == null) {
            return "Order ID is required!";
        }
        if (request.getEarlyDeliveryDate() == null) {
            return "Early delivery date is required!";
        }
        if (request.getAcceptanceDeadline() == null) {
            return "Acceptance deadline is required!";
        }
        if (request.getPrice() < 0) {
            return "Quotation price must be a non-negative number!";
        }
        if (request.getAcceptanceDeadline().isBefore(LocalDate.now())) {
            return "Acceptance deadline must be today or later!";
        }
        if (request.getEarlyDeliveryDate().isBefore(request.getAcceptanceDeadline())) {
            return "Early delivery date must be after the acceptance deadline!";
        }
        return null;
    }
}
