package com.unisew.server.services;

import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface SampleImageService {
    ResponseEntity<ResponseObject> getAllSampleImages();
}
