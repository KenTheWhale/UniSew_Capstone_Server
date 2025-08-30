package com.unisew.server.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePartnerProfileRequest {

    WalletDTO wallet;
    CustomerDTO customer;
    PartnerDTO partner;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class WalletDTO {
        String ownerName;
        String bank;
        String bankAccountNumber;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CustomerDTO {
        String name;
        String businessName;
        String avatarUrl;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PartnerDTO {
        LocalTime startTime;
        LocalTime endTime;
    }
}
