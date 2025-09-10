package com.unisew.server.controllers;

import com.unisew.server.requests.CreateTransactionRequest;
import com.unisew.server.requests.GetPaymentURLRequest;
import com.unisew.server.requests.RefundRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<ResponseObject> getPaymentUrl(@RequestBody GetPaymentURLRequest request, HttpServletRequest httpRequest) {
        return paymentService.getPaymentURL(request, httpRequest);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllTransaction() {
        return paymentService.getAllTransaction();
    }

    @PostMapping("/transaction")
    @PreAuthorize("hasAnyRole('SCHOOL', 'ADMIN')")
    public ResponseEntity<ResponseObject> createTransaction(@RequestBody CreateTransactionRequest request, HttpServletRequest httpRequest) {
        return paymentService.createTransaction(request, httpRequest);
    }

    @PostMapping("/transaction/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> refundTransaction(@RequestBody RefundRequest request, HttpServletRequest httpRequest) {
        return paymentService.refundTransaction(request, httpRequest);
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasAnyRole('SCHOOL', 'DESIGNER', 'GARMENT')")
    public ResponseEntity<ResponseObject> getTransactions(HttpServletRequest httpRequest) {
        return paymentService.getTransactions(httpRequest);
    }

    @PostMapping("/my/transaction")
    @PreAuthorize("hasAnyRole('SCHOOL', 'DESIGNER', 'GARMENT')")
    public ResponseEntity<ResponseObject> getMyTransaction(HttpServletRequest httpRequest) {
        return paymentService.getMyTransaction(httpRequest);
    }

    @PostMapping("/wallet")
    @PreAuthorize("hasAnyRole('SCHOOL', 'DESIGNER', 'GARMENT')")
    public ResponseEntity<ResponseObject> getWalletBalance(HttpServletRequest httpRequest) {
        return paymentService.getWalletBalance(httpRequest);
    }
}
