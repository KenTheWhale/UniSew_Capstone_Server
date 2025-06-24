package com.unisew.server.services;

import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DesignRequestService {
    ResponseEntity<ResponseObject> getAllDesignRequests();
    ResponseEntity<ResponseObject> getDesignRequestById(GetDesignRequestById request);
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest request);
    ResponseEntity<ResponseObject> pickPackage(PickPackageRequest request);
    ResponseEntity<ResponseObject> makeDesignPublic(MakeDesignPublicRequest request);
    ResponseEntity<ResponseObject> createRevisionDesign(CreateRevisionDesignRequest request);
    ResponseEntity<ResponseObject> getAllDesignComments(int designId);
}
