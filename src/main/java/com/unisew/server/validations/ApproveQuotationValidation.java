package com.unisew.server.validations;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Quotation;

import java.time.LocalDate;

public class ApproveQuotationValidation {
    public static String validate(Quotation quotation) {
        if (quotation == null) {
            return "Quotation not found";
        }
        if (quotation.getStatus() == Status.QUOTATION_REJECTED) {
            return "This quotation has already been rejected";
        }
        if (quotation.getStatus() == Status.QUOTATION_APPROVED) {
            return "This quotation has already been approved";
        }
        if (quotation.getAcceptanceDeadline() != null && quotation.getAcceptanceDeadline().isBefore(LocalDate.now())) {
            return "The acceptance deadline has passed";
        }
        return null;
    }
}
