package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DesignItemSize {

    MALE_SHIRT_S("shirt", "S", "male", 0, 0, 0, 0),
    MALE_SHIRT_M("shirt", "M", "male", 0, 0, 0, 0),
    MALE_SHIRT_L("shirt", "L", "male", 0, 0, 0, 0),
    MALE_SHIRT_XL("shirt", "XL", "male", 0, 0, 0, 0),
    MALE_SHIRT_XXL("shirt", "XXL", "male", 0, 0, 0, 0),
    MALE_SHIRT_3XL("shirt", "3XL", "male", 0, 0, 0, 0),
    MALE_SHIRT_4XL("shirt", "4XL", "male", 0, 0, 0, 0),

    FEMALE_SHIRT_S("shirt", "S", "female", 0, 0, 0, 0),
    FEMALE_SHIRT_M("shirt", "M", "female", 0, 0, 0, 0),
    FEMALE_SHIRT_L("shirt", "L", "female", 0, 0, 0, 0),
    FEMALE_SHIRT_XL("shirt", "XL", "female", 0, 0, 0, 0),
    FEMALE_SHIRT_XXL("shirt", "XXL", "female", 0, 0, 0, 0),
    FEMALE_SHIRT_3XL("shirt", "3XL", "female", 0, 0, 0, 0),
    FEMALE_SHIRT_4XL("shirt", "4XL", "female", 0, 0, 0, 0),

    MALE_PANTS_28("pants", "S", "male", 0, 0, 0, 0),
    MALE_PANTS_29("pants", "M", "male", 0, 0, 0, 0),
    MALE_PANTS_30("pants", "L", "male", 0, 0, 0, 0),
    MALE_PANTS_31("pants", "XL", "male", 0, 0, 0, 0),
    MALE_PANTS_32("pants", "XXL", "male", 0, 0, 0, 0),
    MALE_PANTS_33("pants", "3XL", "male", 0, 0, 0, 0),
    MALE_PANTS_34("pants", "4XL", "male", 0, 0, 0, 0),

    FEMALE_PANTS_28("pants", "S", "female", 0, 0, 0, 0),
    FEMALE_PANTS_29("pants", "M", "female", 0, 0, 0, 0),
    FEMALE_PANTS_30("pants", "L", "female", 0, 0, 0, 0),
    FEMALE_PANTS_31("pants", "XL", "female", 0, 0, 0, 0),
    FEMALE_PANTS_32("pants", "XXL", "female", 0, 0, 0, 0),
    FEMALE_PANTS_33("pants", "3XL", "female", 0, 0, 0, 0),
    FEMALE_PANTS_34("pants", "4XL", "female", 0, 0, 0, 0),

    FEMALE_SKIRT_S("skirt", "S", "female", 0, 0, 0, 0),
    FEMALE_SKIRT_M("skirt", "M", "female", 0, 0, 0, 0),
    FEMALE_SKIRT_L("skirt", "L", "female", 0, 0, 0, 0),
    FEMALE_SKIRT_XL("skirt", "XL", "female", 0, 0, 0, 0),
    FEMALE_SKIRT_XXL("skirt", "XXL", "female", 0, 0, 0, 0),
    FEMALE_SKIRT_3XL("skirt", "3XL", "female", 0, 0, 0, 0),
    FEMALE_SKIRT_4XL("skirt", "4XL", "female", 0, 0, 0, 0),
    ;

    private final String type;
    private final String size;
    private final String gender;
    private final int maxHeight;
    private final int minHeight;
    private final int maxWeight;
    private final int minWeight;
}
