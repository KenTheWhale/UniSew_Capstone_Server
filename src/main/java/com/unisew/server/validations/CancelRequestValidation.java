package com.unisew.server.validations;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CancelRequestValidation {
    public static ResponseEntity<ResponseObject> validate(Account account, DesignRequest designRequest) {
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Design request not found", null);
        }

        if (!designRequest.getSchool().getId().equals(account.getCustomer().getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not the owner of the request to cancel", null);
        }

        if (designRequest.getStatus().equals(Status.DESIGN_REQUEST_CANCELED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design request already cancelled", null);
        }

        if (designRequest.getStatus().equals(Status.DESIGN_REQUEST_COMPLETED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design request already complete", null);
        }

        return null;
    }
}
