package com.unisew.server.services;

import com.unisew.server.requests.AdminAccountStatsRequest;
import com.unisew.server.requests.AdminTransactionStatsRequest;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    ResponseEntity<ResponseObject> getAccountStats(AdminAccountStatsRequest request);

    ResponseEntity<ResponseObject> getTransactionStats(AdminTransactionStatsRequest request);

}
