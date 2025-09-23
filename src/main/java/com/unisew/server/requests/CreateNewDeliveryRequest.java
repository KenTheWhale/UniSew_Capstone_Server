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
        int designItemId;
        String frontUrl;
        String backUrl;
        ButtonData buttonData;
        LogoData logoData;
        boolean zipper;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ButtonData {
        int quantity;
        double height;
        double width;
        int holeQty;
        String color;
        String note;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LogoData {
        String attachingTechnique;
        double baseHeight;
        double baseWidth;
        String note;
    }

}
