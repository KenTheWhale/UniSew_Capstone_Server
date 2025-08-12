package com.unisew.server.validations;

import com.unisew.server.requests.AssignMilestoneRequest;
import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.CreateSewingPhaseRequest;
import com.unisew.server.requests.UpdateProductionStatusRequest;

public class OrderValidation {
    public static String validate(CreateOrderRequest request) {
        if (request.getDeliveryId() == null || request.getDeliveryId() <= 0) {
            return "Invalid delivery ID.";
        }
        if (request.getDeadline() == null) {
            return "Deadline cannot be null.";
        }
        if (request.getOrderDetails() == null || request.getOrderDetails().isEmpty()) {
            return "Order details cannot be empty.";
        }
        for (CreateOrderRequest.OrderItem item : request.getOrderDetails()) {
            if (item.getDeliveryItemId() == null || item.getDeliveryItemId() <= 0) {
                return "Invalid delivery item ID in order details.";
            }
            if (item.getSize() == null || item.getSize().isEmpty()) {
                return "Size cannot be null or empty in order details.";
            }
            if (item.getQuantity() <= 0) {
                return "Quantity must be greater than 0 in order details.";
            }
        }
        return null; // No validation errors
    }

    public static String validateUpdateProductionStatus(UpdateProductionStatusRequest request) {
        if (request.getOrderId() == null || request.getOrderId() <= 0) {
            return "Invalid order ID.";
        }
        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            return "Status cannot be null or empty.";
        }

        String[] validStatuses = {
                "fabric_preparation", "cutting", "patching", "ironing",
                "quality_check", "packaging", "sewing", "embroidering",
                "hand_sewing", "delivering", "completed"
        };
        boolean isValidStatus = false;

        for (String validStatus : validStatuses) {
            if (request.getStatus().equals(validStatus)) {
                isValidStatus = true;
                break;
            }
        }
        if (!isValidStatus) {
            return "Invalid status provided.";
        }
        return null;

    }

    public static String validateCreateSewingPhase(CreateSewingPhaseRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            return "Sewing phase name cannot be null or empty.";
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            return "Sewing phase description cannot be null or empty.";
        }
        return null;
    }

    public static String validateAssignMilestone(AssignMilestoneRequest request) {
        if (request.getStartDate() == null) {
            return "Start date is required.";
        }
        if (request.getEndDate() == null) {
            return "End date is required.";
        }
        if (request.getEndDate().isBefore(request.getStartDate()) || request.getEndDate().isEqual(request.getStartDate())) {
            return "End date must be after start date.";
        }
        return null;
    }
}
