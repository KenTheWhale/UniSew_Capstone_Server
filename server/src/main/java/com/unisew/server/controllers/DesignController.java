package com.unisew.server.controllers;

import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/design")
public class DesignController {

    private final DesignService designService;

    @GetMapping("/fabrics")
    public ResponseEntity<ResponseObject> getListFabrics() {
        return designService.getAllFabric();
    }
}
