package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    //ACCOUNT
    ACCOUNT_ACTIVE("active"),
    ACCOUNT_INACTIVE("inactive");

    private final String value;
}
