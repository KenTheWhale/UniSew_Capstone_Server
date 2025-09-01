package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    //ACCOUNT
    ACCOUNT_ACTIVE("active"),
    ACCOUNT_INACTIVE("inactive"),

    //ACCOUNT REQUEST
    ACCOUNT_REQUEST_PENDING("pending"),
    ACCOUNT_REQUEST_REJECTED("rejected"),
    ACCOUNT_REQUEST_APPROVED("approved"),

    //SERVICE
    SERVICE_ACTIVE("active"),

    //DESIGN
    DESIGN_REQUEST_PENDING("pending"),
    DESIGN_REQUEST_PROCESSING("processing"),
    DESIGN_REQUEST_COMPLETED("completed"),
    DESIGN_REQUEST_CANCELED("canceled"),

    //DESIGN QUOTATION
    DESIGN_QUOTATION_PENDING("pending"),
    DESIGN_QUOTATION_SELECTED("selected"),
    DESIGN_QUOTATION_REJECTED("rejected"),

    //ORDER
    ORDER_PENDING("pending"),
    ORDER_CANCELED("canceled"),
    ORDER_PROCESSING("processing"),
    ORDER_DELIVERING("delivering"),
    ORDER_COMPLETED("completed"),

    //GARMENT QUOTATION
    GARMENT_QUOTATION_PENDING("pending"),
    GARMENT_QUOTATION_APPROVED("approved"),
    GARMENT_QUOTATION_REJECTED("rejected"),

    //SEWING PHASE
    SEWING_PHASE_ACTIVE("active"),
    SEWING_PHASE_INACTIVE("inactive"),

    //MILESTONE
    MILESTONE_ASSIGNED("assigned"),
    MILESTONE_PROCESSING("processing"),
    MILESTONE_COMPLETED("completed"),
    MILESTONE_LATE("late"),

    //TRANSACTION
    TRANSACTION_SUCCESS("success"),
    TRANSACTION_FAIL("fail"),

    //FEEDBACK
    FEEDBACK_APPROVED("approved"),
    FEEDBACK_REPORT_UNDER_REVIEW("under-review"),
    FEEDBACK_REPORT_RESOLVED_ACCEPTED("accepted"),
    FEEDBACK_REPORT_RESOLVED_REJECTED("rejected"),

    //WITHDRAW REQUEST
    WITHDRAW_APPROVED("approved"),
    WITHDRAW_REJECTED("rejected"),
    WITHDRAW_PENDING("pending"),

    APPEAL_UNDER_REVIEW("under-review"),
    APPEAL_ACCEPTED("accepted"),
    APPEAL_REJECTED("rejected"),;

    private final String value;
}
