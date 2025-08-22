package com.unisew.server.services;

import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface DesignService {
    //--------------------------------DESIGN_REQUEST-----------------------------------------//
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewListDesignRequest();

    ResponseEntity<ResponseObject> getListDesignRequestBySchool(HttpServletRequest request);

    ResponseEntity<ResponseObject> getDesignRequestDetailForSchool(int id);

    ResponseEntity<ResponseObject> getDesignRequestDetailForDesigner(int id);

    ResponseEntity<ResponseObject> getListDesignRequestByDesigner(HttpServletRequest request);

    ResponseEntity<ResponseObject> pickDesignQuotation(PickDesignQuotationRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateRequestByDeadline(UpdateRequestByDeadline request);

    ResponseEntity<ResponseObject> duplicateRequest(DuplicateRequest request);

    ResponseEntity<ResponseObject> buyRevisionTime(UpdateRevisionTimeRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> cancelRequest(CancelRequest request, HttpServletRequest httpServletRequest);
    //--------------------------------FABRIC-----------------------------------------//
    ResponseEntity<ResponseObject> getAllFabric();

    //--------------------------------DESIGN DELIVERY----------------------------------------//

    ResponseEntity<ResponseObject> getListDeliveries(GetListDeliveryRequest request);

    ResponseEntity<ResponseObject> createNewDelivery(CreateNewDeliveryRequest request);

    ResponseEntity<ResponseObject> addFileUrl(AddFileUrlRequest request);

    //--------------------------------REVISION_REQUEST----------------------------------------//
    ResponseEntity<ResponseObject> createRevisionRequest(CreateRevisionRequest request);

    ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(GetUnUseListRevisionRequest request);


    //--------------------------------SCHOOL_DESIGN----------------------------------------//
    ResponseEntity<ResponseObject> getListSchoolDesign(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> makeDesignFinal(HttpServletRequest httpRequest, MakeDesignFinalRequest request);

    //--------------------------------DESIGN QUOTATION----------------------------------------//
    ResponseEntity<ResponseObject> getQuotationHistory(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpRequest, CreateDesignQuotationRequest request);
}
