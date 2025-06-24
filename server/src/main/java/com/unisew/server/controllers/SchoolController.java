package com.unisew.server.controllers;

import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.ClothService;
import com.unisew.server.services.DesignDraftService;
import com.unisew.server.services.DesignRequestService;
import com.unisew.server.services.SampleImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/v1/design")
public class SchoolController {

    final DesignRequestService designRequestService;
    final DesignDraftService designDraftService;
    final ClothService clothService;
    final SampleImageService sampleImageService;

    @GetMapping("/list-request")
    public ResponseEntity<ResponseObject> listRequest() {
        return designRequestService.getAllDesignRequests();
    }

    @PostMapping("/")
    public ResponseEntity<ResponseObject> addRequest(@RequestBody CreateDesignRequest request) {
        return designRequestService.createDesignRequest(request);
    }

    @PostMapping("/request")
    public ResponseEntity<ResponseObject> getRequestById(@RequestBody GetDesignRequestById request) {
        return designRequestService.getDesignRequestById(request);
    }

    @PostMapping("/package")
    public ResponseEntity<ResponseObject> pickPackage(@RequestBody PickPackageRequest request) {
        return designRequestService.pickPackage(request);
    }

    @PostMapping("/public")
    public ResponseEntity<ResponseObject> makeDesignPublic(@RequestBody MakeDesignPublicRequest request) {
        return designRequestService.makeDesignPublic(request);
    }

    @PostMapping("/design-draft")
    public ResponseEntity<ResponseObject> addDesignDraft(@RequestBody CreateDesignDraftRequest request) {
        return designDraftService.createDesignDraft(request);
    }

    @PostMapping("/final")
    public  ResponseEntity<ResponseObject> makeDesignDraftFinal(@RequestBody SetDesignDraftFinalRequest request) {
        return designDraftService.setDesignDraftFinal(request);
    }
    @PostMapping("/revision")
    public ResponseEntity<ResponseObject> createRevisionRequest(@RequestBody CreateRevisionDesignRequest request) {
        return designRequestService.createRevisionDesign(request);
    }

    @PostMapping("/cloth-list")
    public ResponseEntity<ResponseObject> getListClothByRequestId(@RequestBody GetAllClothByRequestId request) {
        return clothService.getAllClothesByRequestId(request);
    }
    @GetMapping("/sampleImage-list")
    public ResponseEntity<ResponseObject> getListSampleImages(){
        return sampleImageService.getAllSampleImages();
    }

    @GetMapping("/design-request/{id}/comments")
    public ResponseEntity<ResponseObject> getListComments(@PathVariable int id) {
        return designRequestService.getAllDesignComments(id);
    }

}
