package com.unisew.server.controllers;

import com.unisew.server.requests.CancelRequest;
import com.unisew.server.requests.CreateDesignQuotationRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.requests.CreateNewDeliveryRequest;
import com.unisew.server.requests.CreateRevisionRequest;
import com.unisew.server.requests.DuplicateRequest;
import com.unisew.server.requests.GetListDeliveryRequest;
import com.unisew.server.requests.GetUnUseListRevisionRequest;
import com.unisew.server.requests.ImportDesignRequest;
import com.unisew.server.requests.MakeDesignFinalRequest;
import com.unisew.server.requests.PickDesignQuotationRequest;
import com.unisew.server.requests.UpdateRequestByDeadline;
import com.unisew.server.requests.UpdateRevisionTimeRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseObject> updateRequestByDeadline(@RequestBody UpdateRequestByDeadline request) {
        return designService.updateRequestByDeadline(request);
    }

    @PostMapping("/request/duplicate")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> duplicateRequest(@RequestBody DuplicateRequest request) {
        return designService.duplicateRequest(request);
    }

    @PutMapping("/request/revision-time")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> buyRevisionTime(@RequestBody UpdateRevisionTimeRequest request, HttpServletRequest httpRequest) {
        return designService.buyRevisionTime(request, httpRequest);
    }

    @PutMapping("/request/cancel")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> cancelRequest(@RequestBody CancelRequest request, HttpServletRequest httpRequest) {
        return designService.cancelRequest(request, httpRequest);
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> importDesign(@RequestBody ImportDesignRequest request, HttpServletRequest httpRequest){
        return designService.importDesign(request, httpRequest);
    }
    //-------------------DESIGN_DELIVERY---------------------//

    @PostMapping("/deliveries")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListDeliveries(@RequestBody GetListDeliveryRequest request) {
        return designService.getListDeliveries(request);
    }

    @PostMapping("/delivery")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> createNewDelivery(@RequestBody CreateNewDeliveryRequest request) {
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

    //-------------------SCHOOL_DESIGN----------------------------//
    @PostMapping("/final/designs")
    @PreAuthorize("hasAnyRole('SCHOOL', 'DESIGNER')")
    public ResponseEntity<ResponseObject> getListSchoolDesign(HttpServletRequest httpRequest) {
        return designService.getListSchoolDesign(httpRequest);
    }

    @PostMapping("/school/request/final")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> makeDesignFinal(HttpServletRequest httpRequest, @RequestBody MakeDesignFinalRequest request) {
        return designService.makeDesignFinal(httpRequest, request);
    }

    //-------------------DESIGN QUOTATION----------------------------//
    @PostMapping("/pick/quotation")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> pickDesignQuotation(@RequestBody PickDesignQuotationRequest request, HttpServletRequest httpRequest) {
        return designService.pickDesignQuotation(request, httpRequest);
    }

    @PostMapping("/quotation/history")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getQuotationHistory(HttpServletRequest httpRequest) {
        return designService.getQuotationHistory(httpRequest);
    }

    @PostMapping("/quotation")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpRequest, @RequestBody CreateDesignQuotationRequest request) {
        return designService.createQuotation(httpRequest, request);
    }
}
