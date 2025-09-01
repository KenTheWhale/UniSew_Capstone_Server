package com.unisew.server.services.implementors;

import com.unisew.server.models.PlatformConfig;
import com.unisew.server.repositories.PlatformConfigRepo;
import com.unisew.server.requests.CreateConfigDataRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.SystemService;
import com.unisew.server.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final PlatformConfigRepo platformConfigRepo;

    @Override
    public ResponseEntity<ResponseObject> getConfigData() {
        List<PlatformConfig> configs = platformConfigRepo.findAll();
        Map<String, Object> data = new HashMap<>();
        for(PlatformConfig config: configs){
            String key = config.getKey();
            Map<String, Object> value = (Map<String, Object>) config.getValue();
            data.put(key, value);
        }

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    public ResponseEntity<ResponseObject> getConfigDataByName(String name) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> createConfigData(CreateConfigDataRequest request) {
        return null;
    }

}
