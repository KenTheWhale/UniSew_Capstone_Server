package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    //ACCOUNT
    ACCOUNT_ACTIVE("active"),
    ACCOUNT_INACTIVE("inactive"),
    //SERVICE
    SERVICE_ACTIVE("active"),
    //PACKAGES
    PACKAGE_ACTIVE("active"),
    PACKAGE_UN_ACTIVE("un-active"),
    //DESIGN
    DESIGN_REQUEST_CREATED("created"),
    DESIGN_REQUEST_PENDING("pending"),
    DESIGN_REQUEST_APPROVE("approve"),
    DESIGN_REQUEST_CANCEL("cancel"),
    //RECEIPT
    RECEIPT_PENDING("pending"),
    RECEIPT_APPROVED("approved"),
    RECEIPT_REJECTED("rejected");

    private final String value;
}
