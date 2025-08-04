package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendCommentRequest {
    Integer requestId;
    Integer deliveryId;
    String comment;
}