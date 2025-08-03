package com.unisew.server.services;

import com.unisew.server.requests.CreateDesignRequest;
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

}
