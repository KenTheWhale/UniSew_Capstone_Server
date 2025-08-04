package com.unisew.server.controllers;

import com.unisew.server.requests.AddPackageToReceiptRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<ResponseObject> getListDesignRequestByCustomer(HttpServletRequest request) {
        return designService.getListDesignRequestByCustomer(request);
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
    //-------------------FABRICS----------------------------//
    @GetMapping("/fabrics")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListFabrics() {
        return designService.getAllFabric();
    }
}
