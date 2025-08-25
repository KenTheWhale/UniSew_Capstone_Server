package com.unisew.server.requests;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApproveCreateAccountRequest {
    int accountRequestId;
    String status;
}
