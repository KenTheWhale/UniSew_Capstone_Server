package com.unisew.server.controllers;

import com.unisew.server.requests.CreateConfigDataRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.SystemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@Tag(name = "System")
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/config")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCHOOL', 'DESIGNER', 'GARMENT')")
    public ResponseEntity<ResponseObject> getConfigData(){
        return systemService.getConfigData();
    }

    @PostMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createConfigData(@RequestBody CreateConfigDataRequest request){
        return systemService.createConfigData(request);
    }
}
