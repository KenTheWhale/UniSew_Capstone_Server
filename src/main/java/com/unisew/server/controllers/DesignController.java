package com.unisew.server.controllers;

import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/design")
public class DesignController {

    private final DesignService designService;

    //-------------------DESIGN_REQUEST---------------------//

    @PostMapping("/request")
    public ResponseEntity<ResponseObject> createNewRequest(@RequestBody CreateDesignRequest request) {
        return designService.createDesignRequest(request);
    }


    //-------------------FABRICS----------------------------//
    @GetMapping("/fabrics")
    public ResponseEntity<ResponseObject> getListFabrics() {
        return designService.getAllFabric();
    }
}
