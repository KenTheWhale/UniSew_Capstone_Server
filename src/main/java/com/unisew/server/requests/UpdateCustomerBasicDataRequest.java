package com.unisew.server.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerBasicDataRequest {
    String name;
    String business;
    String address;
    String taxCode;
    String phone;
    String avatar;
    String bank;
    String bankNumber;
    String cardOwner;
}
