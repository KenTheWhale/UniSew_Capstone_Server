package com.unisew.server.validations;

import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Gender;
import com.unisew.server.requests.CreateDesignRequest;

import java.util.List;
import java.util.Set;

public class CreateDesignValidation {
    public static String validate(CreateDesignRequest createDesignRequest) {

        Set<String> validTypes = Set.of(
                DesignItemType.SHIRT.getValue(),
                DesignItemType.PANTS.getValue(),
                DesignItemType.SKIRT.getValue()
        );

        Set<String> validDesignTypes = Set.of("upload", "new", "template");

        Set<String> validGenders = Set.of(
                Gender.BOY.getValue().toLowerCase(),
                Gender.GIRL.getValue().toLowerCase()
        );

        if (createDesignRequest.getDesignName() == null || createDesignRequest.getDesignName().isEmpty()) {
            return "Design name is required";
        }

        if (createDesignRequest.getLogoImage() == null || createDesignRequest.getLogoImage().isEmpty()) {
            return "Logo image is required";
        }

        List<CreateDesignRequest.Item> items = createDesignRequest.getDesignItem();
        if (items == null || items.isEmpty()) {
            return "At least one design item is required";
        }

        for (CreateDesignRequest.Item item : items) {

            if (item.getDesignType() == null ||
                    !validDesignTypes.contains(item.getDesignType().toLowerCase())) {
                return "DesignType must be 'upload', 'new', or 'template'";
            }

            if (item.getGender() == null ||
                    !validGenders.contains(item.getGender().toLowerCase())) {
                return "Gender must be 'boy' or 'girl'";
            }

//            if (!item.getItemType().equalsIgnoreCase(DesignItemType.PANTS.getValue())) {
//                return "ClothType must be 'pants' or 'pants'";
//            }

            if (!validTypes.contains(item.getItemType().toLowerCase())) {
                return "ClothType must be 'shirt', 'pants' or 'skirt'";
            }

            if (item.getLogoPosition() == null || item.getLogoPosition().isEmpty()) {
                return "Logo position is required";
            }

            if (item.getFabricId() == null || item.getFabricId() <= 0) {
                return "Fabric Id is required";
            }

        }

        return "";
    }
}

