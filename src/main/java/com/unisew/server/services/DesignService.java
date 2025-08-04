package com.unisew.server.services;

import com.unisew.server.requests.AddPackageToReceiptRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.requests.CreateNewDeliveryRequest;
import com.unisew.server.requests.CreateRevisionRequest;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DesignService {
    //--------------------------------DESIGN_REQUEST-----------------------------------------//
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest);

    ResponseEntity<ResponseObject> viewListDesignRequests();

    ResponseEntity<ResponseObject> getListDesignRequestByCustomerId(int customerId);

    ResponseEntity<ResponseObject> pickPackage(int packageId, int designRequestId);

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
}
