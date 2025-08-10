package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {
    Integer deliveryId;
    Integer garmentId;
    LocalDate deadline;
    String note;
    List<OrderItem> orderDetails;

    @Data
    public static class OrderItem {
        Integer deliveryItemId;
        String size;
        int quantity;
    }
}
