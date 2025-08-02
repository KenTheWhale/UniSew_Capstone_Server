package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DesignItemCategory {

    REGULAR("regular"),
    PHYSICAL("pe"),;

    private final String value;
}
