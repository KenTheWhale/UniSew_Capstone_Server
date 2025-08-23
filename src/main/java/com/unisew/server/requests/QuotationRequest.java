package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuotationRequest {
    Integer orderId;
    LocalDate earlyDeliveryDate;
    LocalDate acceptanceDeadline;
    long price;
    String note;
}
