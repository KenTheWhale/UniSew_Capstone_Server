package com.unisew.server.services;

import com.unisew.server.models.WithdrawRequest;
import com.unisew.server.requests.CreateTransactionRequest;
import com.unisew.server.requests.GetPaymentURLRequest;
import com.unisew.server.requests.RefundRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<ResponseObject> getPaymentURL(GetPaymentURLRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> createTransaction(CreateTransactionRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getAllTransaction();

    ResponseEntity<ResponseObject> refundTransaction(RefundRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getTransactions(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getMyTransaction(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getWalletBalance(HttpServletRequest httpRequest);

}
