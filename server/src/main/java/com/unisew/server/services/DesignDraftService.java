package com.unisew.server.services;

import com.unisew.server.requests.CreateDesignDraftRequest;
import com.unisew.server.requests.SetDesignDraftFinalRequest;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DesignDraftService {
    ResponseEntity<ResponseObject> createDesignDraft(CreateDesignDraftRequest request);
    ResponseEntity<ResponseObject> setDesignDraftFinal(SetDesignDraftFinalRequest request);
}
