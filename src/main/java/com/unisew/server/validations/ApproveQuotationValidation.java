package com.unisew.server.validations;

import com.unisew.server.enums.Status;
import com.unisew.server.models.GarmentQuotation;

import java.time.LocalDate;

public class ApproveQuotationValidation {
    public static String validate(GarmentQuotation garmentQuotation) {
        if (garmentQuotation == null) {
            return "Quotation not found";
        }
        if (garmentQuotation.getStatus() == Status.GARMENT_QUOTATION_REJECTED) {
            return "This quotation has already been rejected";
        }
        if (garmentQuotation.getStatus() == Status.GARMENT_QUOTATION_APPROVED) {
            return "This quotation has already been approved";
        }
        if (garmentQuotation.getAcceptanceDeadline() != null && garmentQuotation.getAcceptanceDeadline().isBefore(LocalDate.now())) {
            return "The acceptance deadline has passed";
        }
        return null;
    }
}
