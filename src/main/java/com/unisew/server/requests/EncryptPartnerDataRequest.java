package com.unisew.server.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EncryptPartnerDataRequest {
    String email;
    String role;
    String address;
    String taxCode;
    String phone;
}
