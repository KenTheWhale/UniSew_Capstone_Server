package com.unisew.server.controllers;

import com.unisew.server.requests.AddPackageToReceiptRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.requests.CreateNewDeliveryRequest;
import com.unisew.server.requests.CreateRevisionRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<ResponseObject> createNewRequest(@RequestBody CreateDesignRequest request) {
        return designService.createDesignRequest(request);
    }

    @GetMapping("/list-request")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListDesignRequest() {
        return designService.viewListDesignRequests();
    }

    @GetMapping("/list-request/{customerId}")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getListDesignRequestByCustomerId(@PathVariable int customerId) {
        return designService.getListDesignRequestByCustomerId(customerId);
    }

    @PostMapping("/packages/{packageId}/{designRequestId}")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> pickPackageRequest(@PathVariable int packageId, @PathVariable int designRequestId) {
        return designService.pickPackage(packageId, designRequestId);
    }

    //------------------RECEIPT---------------------------//
    @GetMapping("/list/receipt/{designRequestId}")
    @PreAuthorize("hasRole('SCHOOL')")
    public ResponseEntity<ResponseObject> getListReceipt(@PathVariable int designRequestId) {
        return designService.getListReceipt(designRequestId);
    }

    @PostMapping("/receipt/package/")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> addPackageToReceipt(@RequestBody AddPackageToReceiptRequest request) {
        return designService.addPackageToReceipt(request);
    }
    //-------------------DESIGN_DELIVERY---------------------//

    @GetMapping("/deliveries/{designRequestId}")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListDeliveries(@PathVariable int designRequestId) {
        return designService.getListDeliveries(designRequestId);
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
    public ResponseEntity<ResponseObject> getUnUseListRevisionByRequestId(@PathVariable int requestId) {
        return designService.getAllUnUsedRevisionRequest(requestId);
    }
}
