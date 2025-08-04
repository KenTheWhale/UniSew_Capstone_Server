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

    ResponseEntity<ResponseObject> pickPackage(int packageId, int designRequestId);

    ResponseEntity<ResponseObject> updateRequestByDeadline(int requestId, String type);


    //--------------------------------FABRIC-----------------------------------------//
    ResponseEntity<ResponseObject> getAllFabric();

    //--------------------------------RECEIPT----------------------------------------//
    ResponseEntity<ResponseObject> getListReceipt(int designRequestId);

    ResponseEntity<ResponseObject> addPackageToReceipt(AddPackageToReceiptRequest request);

    ResponseEntity<ResponseObject> getListDeliveries(int designRequestId);

    ResponseEntity<ResponseObject> createNewDelivery(CreateNewDeliveryRequest request);

    //--------------------------------REVISION_REQUEST----------------------------------------//
    ResponseEntity<ResponseObject> createRevisionRequest(CreateRevisionRequest request);

    ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(int requestId);

    //--------------------------------DESIGN_COMMENT----------------------------------------//
    ResponseEntity<ResponseObject> getListDesignComment(int designRequestId);

    ResponseEntity<ResponseObject> sendComment(HttpServletRequest request, SendCommentRequest sendCommentRequest);

}
