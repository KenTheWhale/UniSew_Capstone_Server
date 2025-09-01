package com.unisew.server.services;

import com.unisew.server.requests.CreateConfigDataRequest;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface SystemService {
    ResponseEntity<ResponseObject> getConfigData();

    ResponseEntity<ResponseObject> createConfigData(CreateConfigDataRequest request);

    ResponseEntity<ResponseObject> getConfigDataByName(String name);
}
