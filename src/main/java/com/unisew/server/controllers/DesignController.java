package com.unisew.server.controllers;

import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/design")
@Tag(name = "Design")
public class DesignController {

    private final DesignService designService;

    //-------------------DESIGN_REQUEST---------------------//

    @PostMapping("/request")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> createNewRequest(@RequestBody CreateDesignRequest request, HttpServletRequest httpRequest) {
        return designService.createDesignRequest(request, httpRequest);
    }

    @GetMapping("/pending/request")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListDesignRequest() {
        return designService.viewListDesignRequest();
    }

    @PostMapping("/school/request")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getListDesignRequestByCustomer(HttpServletRequest request) {
        return designService.getListDesignRequestByCustomer(request);
    }

    @PostMapping("/pick-packages")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> pickPackageRequest(@RequestBody PickPackageRequest request) {
        return designService.pickPackage(request);
    }

    @PutMapping("/request/deadline")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> updateRequestByDeadline(@RequestBody UpdateRequestByDeadline request){
        return designService.updateRequestByDeadline(request);
    }

    @PostMapping("/request/duplicate")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> duplicateRequest(@RequestBody DuplicateRequest request){
        return designService.duplicateRequest(request);
    }

    //------------------RECEIPT---------------------------//
    @PostMapping("/list/receipt")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getListReceipt(@RequestBody GetListReceiptRequest request) {
        return designService.getListReceipt(request);
    }

    @PostMapping("/receipt/package/")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> addPackageToReceipt(@RequestBody AddPackageToReceiptRequest request) {
        return designService.addPackageToReceipt(request);
    }
    //-------------------DESIGN_DELIVERY---------------------//

    @PostMapping("/deliveries")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListDeliveries(@RequestBody GetListDeliveryRequest request) {
        return designService.getListDeliveries(request);
    }

    @PostMapping("/delivery")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> createNewDelivery(@RequestBody CreateNewDeliveryRequest request){
        return designService.createNewDelivery(request);
    }

    //-------------------FABRICS----------------------------//
    @GetMapping("/fabrics")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListFabrics() {
        return designService.getAllFabric();
    }

    //-------------------REVISION_REQUEST----------------------------//
    @PostMapping("/revision")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> createNewRevision(@RequestBody CreateRevisionRequest request) {
        return designService.createRevisionRequest(request);
    }

    @GetMapping("/list-revision/{requestId}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getUnUseListRevisionByRequestId(GetUnUseListRevisionRequest request) {
        return designService.getAllUnUsedRevisionRequest(request);
    }
    //-------------------DESIGN_COMMENT----------------------------//
    @PostMapping("/list-comment/{requestId}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListComment(@RequestBody GetListCommentRequest request) {
        return designService.getListDesignComment(request);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> sendComment(@RequestBody SendCommentRequest request, HttpServletRequest httpRequest) {
        return designService.sendComment(httpRequest, request);
    }

    //-------------------SCHOOL_DESIGN----------------------------//
    @PostMapping("/school-design/list")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getListSchoolDesign(HttpServletRequest httpRequest, @RequestBody GetListSchoolDesignRequest request){
        return designService.getListSchoolDesign(httpRequest, request);
    }

    @PostMapping("/school-design")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> makeDesignFinal(HttpServletRequest httpRequest, @RequestBody MakeDesignFinalRequest request){
        return designService.makeDesignFinal(httpRequest, request);
    }

    //-------------------PACKAGE----------------------------//
    @PostMapping("/package-list")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListPackage(HttpServletRequest httpRequest){
        return designService.getListPackage(httpRequest);
    }

    @PostMapping("/new-package")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> createPackages(HttpServletRequest httpRequest, @RequestBody CreatePackagesRequest request){
        return designService.createPackages(httpRequest, request);
    }

    @PutMapping("/packages/status")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> changePackageStatus(@RequestBody ChangePackageStatusRequest request){
        return designService.changePackageStatus(request);
    }
}
