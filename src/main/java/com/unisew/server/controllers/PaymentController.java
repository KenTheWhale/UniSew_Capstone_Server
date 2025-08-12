package com.unisew.server.controllers;

import com.unisew.server.requests.CreateTransactionRequest;
import com.unisew.server.requests.GetPaymentURLRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "Payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/url")
    @PreAuthorize("hasAnyRole('SCHOOL', 'DESIGNER')")
    public ResponseEntity<ResponseObject> getPaymentUrl(@RequestBody GetPaymentURLRequest request, HttpServletRequest httpRequest){
        return paymentService.getPaymentURL(request, httpRequest);
    }
}
