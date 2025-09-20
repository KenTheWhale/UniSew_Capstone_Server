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
public class ImportDesignRequest {

    DesignData designData;
    List<DesignItemData> designItemDataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DesignData{
        String name;
        String logoImage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DesignItemData{
        String type;
        String category;
        String logoPosition;
        String color;
        String gender;
        int fabricId;
        String frontImage;
        String backImage;
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
        int height;
        int width;
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
        int baseHeight;
        int baseWidth;
        String note;
    }
}
