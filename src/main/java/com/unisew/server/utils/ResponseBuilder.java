package com.unisew.server.utils;

import com.unisew.server.responses.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static ResponseEntity<ResponseObject> build(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status)
                .body(
                        ResponseObject.builder()
                                .message(message)
                                .body(data)
                                .build()
                );
    }
}
