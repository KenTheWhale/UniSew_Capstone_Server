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
public class CreateDesignRequest {
    String designName;
    String logoImage;
    List<Item> designItem;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Item {
        String designType;
        String gender;
        String clothType;
        String category;
        Integer fabricId;
        String logoPosition;
        String color;
        String note;
        List<Image> uploadImage;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class Image {
            String url;
        }
    }
}
