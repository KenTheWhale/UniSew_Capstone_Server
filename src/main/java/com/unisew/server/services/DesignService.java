package com.unisew.server.services;

import com.unisew.server.requests.AddPackageToReceiptRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface DesignService {
    //--------------------------------DESIGN_REQUEST-----------------------------------------//
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewListDesignRequest();

    ResponseEntity<ResponseObject> getListDesignRequestByCustomer(HttpServletRequest request);

    ResponseEntity<ResponseObject> pickPackage(int packageId, int designRequestId);

    //--------------------------------FABRIC-----------------------------------------//
    ResponseEntity<ResponseObject> getAllFabric();

    //--------------------------------Receipt----------------------------------------//
    ResponseEntity<ResponseObject> getListReceipt(int designRequestId);

    ResponseEntity<ResponseObject> addPackageToReceipt(AddPackageToReceiptRequest request);
}
