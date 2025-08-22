package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiveFeedbackRequest {
    Integer requestId;
    Integer orderId;
    Integer rating;
    String content;
    boolean report;
    String imageUrl;
}
