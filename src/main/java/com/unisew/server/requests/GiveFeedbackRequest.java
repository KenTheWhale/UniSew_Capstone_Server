package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
    List<String> imageUrls;
}
