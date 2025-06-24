package com.unisew.server.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePackageRequest {

    String name;
    String headerContent;
    int deliveryDuration;
    int revisionTime;
    long fee;
    Integer designerId;
    List<Integer> serviceIds;
}
