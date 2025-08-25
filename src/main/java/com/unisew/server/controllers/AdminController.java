package com.unisew.server.controllers;

import com.unisew.server.requests.AdminAccountStatsRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/account/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAccountStats(@RequestBody AdminAccountStatsRequest request) {
        return adminService.getAccountStats(request);
    }
}
