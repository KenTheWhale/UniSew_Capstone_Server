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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final PlatformConfigRepo platformConfigRepo;

    @Override
    public ResponseEntity<ResponseObject> getConfigData() {
        PlatformConfig latestConfig = platformConfigRepo.findFirstByOrderByIdDesc().orElse(null);
        if(latestConfig == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "No config", null);

        Map<String, Object> data = new HashMap<>();
        data.put("id", latestConfig.getId());
        data.put("serviceFee", latestConfig.getFeePercentage());
        data.put("videoLimit", latestConfig.getVideoMaxSize());
        data.put("imageLimit", latestConfig.getImageMaxSize());
        data.put("assignedMilestoneLimit", latestConfig.getMaxMilestone());
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    public ResponseEntity<ResponseObject> createConfigData(CreateConfigDataRequest request) {
        String error;
        if(!(error = validateCreateConfigData(request)).isEmpty()){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        platformConfigRepo.save(
                PlatformConfig.builder()
                        .feePercentage(request.getServiceFee())
                        .imageMaxSize(request.getMaxImageSize())
                        .videoMaxSize(request.getMaxVideoSize())
                        .maxMilestone(request.getMaxMilestone())
                        .build()
        );
        return ResponseBuilder.build(HttpStatus.CREATED, "Update successfully", null);
    }

    private String validateCreateConfigData(CreateConfigDataRequest request){
        if(request.getServiceFee() < 0) return "Service fee invalid";
        if(request.getMaxImageSize() < 0) return "Image size invalid";
        if(request.getMaxVideoSize() < 0) return "Video size invalid";
        if(request.getMaxMilestone() < 0) return "Milestone limit invalid";
        return "";
    }
}
