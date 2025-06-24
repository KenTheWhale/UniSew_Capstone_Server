package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    ACCOUNT_ACTIVE("active"),
    SERVICE_ACTIVE("active"),
    PACKAGE_ACTIVE("active"),
    PACKAGE_UN_ACTIVE("un-active"),
    DESIGN_REQUEST_CREATED("created"),
    DESIGN_REQUEST_DRAFT("draft"),
    DESIGN_REQUEST_PENDING("pending"),
    DESIGN_REQUEST_APPROVE("approve");

    private final String value;
}
