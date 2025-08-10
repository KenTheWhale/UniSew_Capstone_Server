package com.unisew.server.validations;

import com.unisew.server.requests.CreateOrderRequest;

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
}
