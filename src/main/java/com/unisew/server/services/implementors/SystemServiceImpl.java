package com.unisew.server.services.implementors;

import com.unisew.server.repositories.PlatformConfigRepo;
import com.unisew.server.requests.CreateConfigDataRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final PlatformConfigRepo platformConfigRepo;


    @Override
    public ResponseEntity<ResponseObject> getConfigData() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> createConfigData(CreateConfigDataRequest request) {
        return null;
    }
}
