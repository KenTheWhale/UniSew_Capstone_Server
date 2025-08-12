package com.unisew.server.validations;

import com.unisew.server.requests.AssignMilestoneRequest;
import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.CreateSewingPhaseRequest;

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
        if (request.getPhaseList().isEmpty()) {
            return "At least one sewing phase must be selected.";
        }
        for (AssignMilestoneRequest.Phase phase : request.getPhaseList()) {
            if (phase.getId() == null || phase.getId() <= 0) {
                return "Invalid sewing phase ID.";
            }

                if (phase.getStartDate() == null) {
                    return "Start date cannot be null for milestones.";
                }
                if (phase.getEndDate() == null) {
                    return "End date cannot be null for milestones.";
                }
                if (phase.getStage() <= 0) {
                    return "Stage must be greater than 0 for milestones.";
                }

        }
        return null;
    }
}
