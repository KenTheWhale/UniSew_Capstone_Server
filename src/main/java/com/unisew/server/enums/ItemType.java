package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemType {

    PANTS("pants"),
    SHIRT("shirt"),
    SKIRT("skirt");

    private final String value;
}
