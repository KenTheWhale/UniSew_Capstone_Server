package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {

    ORDER("order"),
    DESIGN("design"),
    ORDER_RETURN("order_return"),
    DESIGN_RETURN("design_return"),
    WALLET("wallet"),
    WALLET_DISBURSEMENT("disbursement"),
    DEPOSIT("deposit"),
    WITHDRAW("withdraw");

    private final String value;
}
