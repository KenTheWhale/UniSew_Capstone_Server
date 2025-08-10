package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    //ACCOUNT
    ACCOUNT_ACTIVE("active"),
    ACCOUNT_INACTIVE("inactive"),

    //ACCOUNT REQUEST
    ACCOUNT_REQUEST_PENDING("pending"),
    ACCOUNT_REQUEST_PENDING_VERIFIED("pending verified"),
    ACCOUNT_REQUEST_COMPLETED("completed"),

    //SERVICE
    SERVICE_ACTIVE("active"),

    //DESIGN
    DESIGN_REQUEST_CREATED("created"),
    DESIGN_REQUEST_PAID("paid"),
    DESIGN_REQUEST_COMPLETED("completed"),
    DESIGN_REQUEST_CANCELED("canceled"),

    //DESIGN QUOTATION
    DESIGN_QUOTATION_PENDING("pending"),
    DESIGN_QUOTATION_SELECTED("selected"),
    DESIGN_QUOTATION_REJECTED("rejected"),

    //ORDER
    ORDER_PENDING("pending"),
    ORDER_CANCELED("canceled"),
    ORDER_PROCESSING("processing"),
    ORDER_FABRIC_PREPARATION("fabric_preparation"),
    ORDER_CUTTING("cutting"),
    ORDER_PATCHING("patching"),
    ORDER_IRONING("ironing"),
    ORDER_QUALITY_CHECK("quality_check"),
    ORDER_PACKAGING("packaging"),
    ORDER_SEWING("sewing"),
    ORDER_EMBROIDERING("embroidering"),
    ORDER_HAND_SEWING("hand_sewing"),
    ORDER_DELIVERING("delivering"),
    ORDER_COMPLETED("completed"),

    //GARMENT QUOTATION
    GARMENT_QUOTATION_PENDING("pending"),
    GARMENT_QUOTATION_APPROVED("approved"),
    GARMENT_QUOTATION_REJECTED("rejected");



    private final String value;
}
