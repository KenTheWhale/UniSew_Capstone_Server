package com.unisew.server.services;

import com.unisew.server.requests.GetAllClothByRequestId;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface ClothService {
    ResponseEntity<ResponseObject> getAllClothesByRequestId(GetAllClothByRequestId request);
    ResponseEntity<ResponseObject> getAllClothes();
}
