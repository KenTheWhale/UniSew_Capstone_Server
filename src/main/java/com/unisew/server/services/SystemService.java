package com.unisew.server.services;

import com.unisew.server.requests.CreateConfigDataRequest;
import com.unisew.server.requests.UpdateGarmentFabricRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface SystemService {
    ResponseEntity<ResponseObject> getConfigData();

    ResponseEntity<ResponseObject> updateConfigData(CreateConfigDataRequest request);

    ResponseEntity<ResponseObject> getConfigDataByKey(String key);

    ResponseEntity<ResponseObject> getGarmentFabric(HttpServletRequest request);

    ResponseEntity<ResponseObject> getGarmentFabricForQuotation(int orderId, HttpServletRequest request);

    ResponseEntity<ResponseObject> updateGarmentFabric(UpdateGarmentFabricRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> deleteGarmentFabric(int fabricId, HttpServletRequest request);
}
