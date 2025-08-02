package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemCategory {

    REGULAR("regular"),
    PHYSICAL("pe"),;

    private final String value;
}
