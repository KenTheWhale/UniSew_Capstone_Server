package com.unisew.server.services;

import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.responses.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface OrderService {


    ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request);
}
