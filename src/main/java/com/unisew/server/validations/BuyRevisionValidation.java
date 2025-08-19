package com.unisew.server.validations;


import com.unisew.server.models.DesignQuotation;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.requests.UpdateRevisionTimeRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuyRevisionValidation {

    public static String validate(DesignRequest designRequest, UpdateRevisionTimeRequest request, DesignQuotation designQuotation) {

        if(request.getRevisionTime() < 0 ) {
            return "Revision time cannot be less than zero";
        }

        if (designRequest.getRevisionTime() != 0){
            return "You can not buy revision";
        }
        if (request.getRevisionTime() * designQuotation.getExtraRevisionPrice() >= 200000000){
            return "Total revision price must not exceed 200,000,000";
        }
        return null;
    }
}
