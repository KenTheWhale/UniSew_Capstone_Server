package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    BOY("boy"),
    GIRL("girl");

    private final String value;
}
