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
public class AddPackageToReceiptRequest {
    int designRequestId;
    List<Integer> packageId;
    LocalDate acceptanceDeadline;
}
