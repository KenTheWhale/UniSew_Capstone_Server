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

    //PACKAGES
    PACKAGE_ACTIVE("active"),
    PACKAGE_INACTIVE("inactive"),
    PACKAGE_DELETE("delete"),
    PACKAGE_PENDING_DELETE("pending delete"),

    //DESIGN
    DESIGN_REQUEST_CREATED("created"),
    DESIGN_REQUEST_PENDING("pending"),
    DESIGN_REQUEST_APPROVE("approve"),
    DESIGN_REQUEST_CANCEL("cancel"),

    //RECEIPT
    RECEIPT_PENDING("pending"),
    RECEIPT_APPROVED("approved"),
    RECEIPT_REJECTED("rejected"),

    //ORDER
    ORDER_PENDING("pending"),
    ORDER_APPROVED("approved"),
    ORDER_REJECTED("rejected"),
    ORDER_COMPLETED("completed"),

    //QUOTATION
    QUOTATION_PENDING("pending"),
    QUOTATION_APPROVED("approved"),
    QUOTATION_REJECTED("rejected");



    private final String value;
}
