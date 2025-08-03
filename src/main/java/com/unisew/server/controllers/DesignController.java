package com.unisew.server.controllers;

import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/design")
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

    @
    //-------------------FABRICS----------------------------//
    @GetMapping("/fabrics")
    @PreAuthorize("hasRole('SCHOOL') or hasRole('DESIGNER')")
    public ResponseEntity<ResponseObject> getListFabrics() {
        return designService.getAllFabric();
    }
}
