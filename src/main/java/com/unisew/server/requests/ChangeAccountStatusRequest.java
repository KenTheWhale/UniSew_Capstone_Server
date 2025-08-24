package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeAccountStatusRequest {
    int accountId;
    String status;
    String reason;
    LocalDate endDate;
}