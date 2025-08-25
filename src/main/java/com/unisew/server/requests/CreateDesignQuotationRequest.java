package com.unisew.server.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDesignQuotationRequest {
    int designRequestId;
    String note;
    int deliveryWithIn;
    int revisionTime;
    long extraRevisionPrice;
    long price;
    LocalDate acceptanceDeadline;
}
