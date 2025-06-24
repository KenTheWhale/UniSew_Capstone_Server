package com.unisew.server.controllers;

import com.unisew.server.requests.CreatePackageRequest;
import com.unisew.server.requests.CreateServiceRequest;
import com.unisew.server.requests.UpdatePackageRequest;
import com.unisew.server.requests.UpdateServiceRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/designer/list")
    public ResponseEntity<ResponseObject> getAllDesignerProfile() {
        return profileService.getAllDesignerProfile();
    }

    @GetMapping("/garment/list")
    public ResponseEntity<ResponseObject> getAllGarmentProfile() {
        return profileService.getAllGarmentProfile();
    }

    @GetMapping("/service")
    public ResponseEntity<ResponseObject> getAllService() {
        return profileService.getAllService();
    }

    @PostMapping("/service")
    public ResponseEntity<ResponseObject> createService(@RequestBody CreateServiceRequest request) {
        return profileService.createService(request);
    }

    @PutMapping("/service")
    public ResponseEntity<ResponseObject> updateService(@RequestBody UpdateServiceRequest request) {
        return profileService.updateService(request);
    }

    @GetMapping("/package/{id}")
    public ResponseEntity<ResponseObject> getPackageInfo(@PathVariable("id") int id) {
        return profileService.getPackageInfo(id);
    }

    @PostMapping("/package")
    public ResponseEntity<ResponseObject> createPackage(@RequestBody CreatePackageRequest request) {
        return profileService.createPackage(request);
    }

    @PutMapping("/package")
    public ResponseEntity<ResponseObject> updatePackage(@RequestBody UpdatePackageRequest request) {
        return profileService.updatePackage(request);
    }

    @DeleteMapping("/package/{id}")
    public ResponseEntity<ResponseObject> disablePackage(@PathVariable("id") int id) {
        return profileService.disablePackage(id);
    }
}
