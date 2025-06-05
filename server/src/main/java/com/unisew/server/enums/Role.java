package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    PLATFORM_ADMIN("padmin"),
    DESIGNER("designer"),
    SCHOOL_ADMIN("sadmin"),
    GARMENT_FACTORY("garment");

    private final String value;
}
