package com.unisew.server.services;

import com.unisew.server.responses.ResponseObject;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

public interface DesignService {
    //--------------------------------DESIGN_REQUEST-----------------------------------------//
    //--------------------------------FABRIC-----------------------------------------//
    public ResponseEntity<ResponseObject> getAllFabric();
}
