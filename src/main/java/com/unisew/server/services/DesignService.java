package com.unisew.server.services;

import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface DesignService {
    //--------------------------------DESIGN_REQUEST-----------------------------------------//
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewListDesignRequest();

    ResponseEntity<ResponseObject> getListDesignRequestByCustomer(HttpServletRequest request);

    ResponseEntity<ResponseObject> pickPackage(PickPackageRequest request);

    ResponseEntity<ResponseObject> updateRequestByDeadline(UpdateRequestByDeadline request);

    ResponseEntity<ResponseObject> duplicateRequest(DuplicateRequest request);

    //--------------------------------FABRIC-----------------------------------------//
    ResponseEntity<ResponseObject> getAllFabric();

    //--------------------------------RECEIPT----------------------------------------//
    ResponseEntity<ResponseObject> getListReceipt(GetListReceiptRequest request);

    ResponseEntity<ResponseObject> addPackageToReceipt(AddPackageToReceiptRequest request);

    ResponseEntity<ResponseObject> getListDeliveries(GetListDeliveryRequest request);

    ResponseEntity<ResponseObject> createNewDelivery(CreateNewDeliveryRequest request);

    //--------------------------------REVISION_REQUEST----------------------------------------//
    ResponseEntity<ResponseObject> createRevisionRequest(CreateRevisionRequest request);

    ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(GetUnUseListRevisionRequest request);

    //--------------------------------DESIGN_COMMENT----------------------------------------//
    ResponseEntity<ResponseObject> getListDesignComment(GetListCommentRequest request);

    ResponseEntity<ResponseObject> sendComment(HttpServletRequest request, SendCommentRequest sendCommentRequest);


}
