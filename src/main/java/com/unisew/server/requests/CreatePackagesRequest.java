package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePackagesRequest {
    int deliveryDuration;
    int revisionTime;
    long fee;
    String headerContent;
    String name;
    String status;
}
