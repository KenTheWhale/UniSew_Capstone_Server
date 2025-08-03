package com.unisew.server.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddPackageToReceiptRequest {
    int designRequestId;
    int packageId;
    LocalDate acceptanceDeadline;
}
