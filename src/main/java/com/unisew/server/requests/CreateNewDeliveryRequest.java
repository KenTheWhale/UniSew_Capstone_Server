package com.unisew.server.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNewDeliveryRequest {
    int designRequestId;
    int revisionId;
    String name;
    boolean revision;
    String note;
    List<DeliveryItems> itemList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeliveryItems {
        int logoHeight;
        int logoWidth;
        int designItemId;
        String frontUrl;
        String backUrl;
    }

}
