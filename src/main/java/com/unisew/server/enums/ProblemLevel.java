package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProblemLevel {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    SERIOUS("serious");

    private final String value;
}
