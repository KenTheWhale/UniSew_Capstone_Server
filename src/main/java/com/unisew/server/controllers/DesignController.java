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
    public ResponseEntity<ResponseObject> getListDesignRequestBySchool(HttpServletRequest request) {
        return designService.getListDesignRequestBySchool(request);
    }

    @GetMapping("/school/request/detail")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getDesignRequestDetailForSchool(@RequestParam int id) {
        return designService.getDesignRequestDetailForSchool(id);
    }

    @PostMapping("/designer/request")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListDesignRequestByDesigner(HttpServletRequest request) {
        return designService.getListDesignRequestByDesigner(request);
    }

    @GetMapping("/designer/request/detail")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getDesignRequestDetailForDesigner(@RequestParam int id) {
        return designService.getDesignRequestDetailForDesigner(id);
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

    @PostMapping("/revision/list")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getUnUseListRevisionByRequestId(@RequestBody GetUnUseListRevisionRequest request) {
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

    @PostMapping("/school/request/final")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> makeDesignFinal(HttpServletRequest httpRequest, @RequestBody MakeDesignFinalRequest request){
        return designService.makeDesignFinal(httpRequest, request);
    }

    //-------------------DESIGN QUOTATION----------------------------//
    @PostMapping("/pick/quotation")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> pickDesignQuotation(@RequestBody PickDesignQuotationRequest request) {
        return designService.pickDesignQuotation(request);
    }

    @PostMapping("/quotation/history")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getQuotationHistory(HttpServletRequest httpRequest){
        return designService.getQuotationHistory(httpRequest);
    }

    @PostMapping("/quotation")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpRequest, @RequestBody CreateDesignQuotationRequest request){
        return designService.createQuotation(httpRequest, request);
    }
}
