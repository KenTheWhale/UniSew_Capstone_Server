package com.unisew.server.requests;

import com.unisew.server.enums.ClothCategory;
import com.unisew.server.enums.ClothType;
import com.unisew.server.enums.Gender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDesignRequest {
    int schoolId;
    List<Cloth> clothes;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Cloth {
        List<Image> images;
        int templateId;
        ClothType type;
        ClothCategory category;
        String logoImage;
        String logoPosition;
        Gender gender;
        String color;
        String note;
        String designType;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Image {
        String url;
    }

}
