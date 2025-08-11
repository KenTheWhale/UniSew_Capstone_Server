package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryItemSize {

    // Male shirts (Ages 6-11)
    MALE_SHIRT_S("shirt", "S", "male", 125, 115, 25, 18),
    MALE_SHIRT_M("shirt", "M", "male", 135, 125, 32, 25),
    MALE_SHIRT_L("shirt", "L", "male", 145, 135, 40, 32),
    MALE_SHIRT_XL("shirt", "XL", "male", 155, 145, 50, 40),
    MALE_SHIRT_XXL("shirt", "XXL", "male", 165, 155, 60, 50),
    MALE_SHIRT_3XL("shirt", "3XL", "male", 170, 165, 65, 60),
    MALE_SHIRT_4XL("shirt", "4XL", "male", 175, 170, 70, 65),

    // Female shirts (Ages 6-11)
    FEMALE_SHIRT_S("shirt", "S", "female", 125, 115, 25, 18),
    FEMALE_SHIRT_M("shirt", "M", "female", 135, 125, 32, 25),
    FEMALE_SHIRT_L("shirt", "L", "female", 145, 135, 40, 32),
    FEMALE_SHIRT_XL("shirt", "XL", "female", 155, 145, 50, 40),
    FEMALE_SHIRT_XXL("shirt", "XXL", "female", 165, 155, 60, 50),
    FEMALE_SHIRT_3XL("shirt", "3XL", "female", 170, 165, 65, 60),
    FEMALE_SHIRT_4XL("shirt", "4XL", "female", 175, 170, 70, 65),

    // Male pants (Ages 6-11)
    MALE_PANTS_S("pants", "S", "male", 125, 115, 25, 18),
    MALE_PANTS_M("pants", "M", "male", 135, 125, 32, 25),
    MALE_PANTS_L("pants", "L", "male", 145, 135, 40, 32),
    MALE_PANTS_XL("pants", "XL", "male", 155, 145, 50, 40),
    MALE_PANTS_XXL("pants", "XXL", "male", 165, 155, 60, 50),
    MALE_PANTS_3XL("pants", "3XL", "male", 170, 165, 65, 60),
    MALE_PANTS_4XL("pants", "4XL", "male", 175, 170, 70, 65),

    // Female pants (Ages 6-11)
    FEMALE_PANTS_S("pants", "S", "female", 125, 115, 25, 18),
    FEMALE_PANTS_M("pants", "M", "female", 135, 125, 32, 25),
    FEMALE_PANTS_L("pants", "L", "female", 145, 135, 40, 32),
    FEMALE_PANTS_XL("pants", "XL", "female", 155, 145, 50, 40),
    FEMALE_PANTS_XXL("pants", "XXL", "female", 165, 155, 60, 50),
    FEMALE_PANTS_3XL("pants", "3XL", "female", 170, 165, 65, 60),
    FEMALE_PANTS_4XL("pants", "4XL", "female", 175, 170, 70, 65),

    // Female skirts (Ages 6-11)
    FEMALE_SKIRT_S("skirt", "S", "female", 125, 115, 25, 18),
    FEMALE_SKIRT_M("skirt", "M", "female", 135, 125, 32, 25),
    FEMALE_SKIRT_L("skirt", "L", "female", 145, 135, 40, 32),
    FEMALE_SKIRT_XL("skirt", "XL", "female", 155, 145, 50, 40),
    FEMALE_SKIRT_XXL("skirt", "XXL", "female", 165, 155, 60, 50),
    FEMALE_SKIRT_3XL("skirt", "3XL", "female", 170, 165, 65, 60),
    FEMALE_SKIRT_4XL("skirt", "4XL", "female", 175, 170, 70, 65);

    private final String type;
    private final String size;
    private final String gender;
    private final int maxHeight;
    private final int minHeight;
    private final int maxWeight;
    private final int minWeight;
}
