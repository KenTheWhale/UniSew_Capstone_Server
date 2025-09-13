package com.unisew.server.controllers;

import com.unisew.server.requests.CreateConfigDataRequest;
import com.unisew.server.requests.UpdateGarmentFabricRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.SystemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/config/key")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCHOOL', 'DESIGNER', 'GARMENT')")
    public ResponseEntity<ResponseObject> getConfigDataByKey(@RequestParam String k){
        return systemService.getConfigDataByKey(k);
    }

    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateConfigData(@RequestBody CreateConfigDataRequest request){
        return systemService.updateConfigData(request);
    }

    @PostMapping("/garment/fabric")
    @PreAuthorize("hasAnyRole('GARMENT', 'SCHOOL')")
    public ResponseEntity<ResponseObject> getGarmentFabric(HttpServletRequest request){
        return systemService.getGarmentFabric(request);
    }

    @GetMapping("/garment/fabric/quotation")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> getGarmentFabricForQuotation(@RequestParam String orderId){
        return systemService.getGarmentFabricForQuotation(orderId);
    }

    @PutMapping("/garment/fabric")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> updateGarmentFabric(@RequestBody UpdateGarmentFabricRequest request, HttpServletRequest httpRequest){
        return systemService.updateGarmentFabric(request, httpRequest);
    }

    @PutMapping("/garment/fabric/remove")
    @PreAuthorize("hasRole('GARMENT')")
    public ResponseEntity<ResponseObject> deleteGarmentFabric(@RequestParam int fabricId, HttpServletRequest request){
        return systemService.deleteGarmentFabric(fabricId, request);
    }
}
