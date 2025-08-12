package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PickDesignQuotationRequest {
    int designQuotationId;
    int designRequestId;
    int extraRevision;
    long serviceFee;
}
