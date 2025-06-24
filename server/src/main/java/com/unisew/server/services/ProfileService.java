package com.unisew.server.services;

import com.unisew.server.requests.CreatePackageRequest;
import com.unisew.server.requests.CreateProfileRequest;
import com.unisew.server.requests.CreateServiceRequest;
import com.unisew.server.requests.UpdatePackageRequest;
import com.unisew.server.requests.UpdateServiceRequest;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface ProfileService {

    ResponseEntity<ResponseObject> getAllDesignerProfile();

    ResponseEntity<ResponseObject> getAllGarmentProfile();

    ResponseEntity<ResponseObject> getPackageInfo(int id);

    ResponseEntity<ResponseObject> createPackage(CreatePackageRequest request);

    ResponseEntity<ResponseObject> updatePackage(UpdatePackageRequest request);

    ResponseEntity<ResponseObject> disablePackage(int id);

    ResponseEntity<ResponseObject> getAllService();

    ResponseEntity<ResponseObject> createService(CreateServiceRequest request);

    ResponseEntity<ResponseObject> updateService(UpdateServiceRequest request);

}
