package com.unisew.server.services.implementors;

import com.unisew.server.configurations.VNPayConfig;
import com.unisew.server.enums.PaymentType;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Transaction;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.CustomerRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.OrderRepo;
import com.unisew.server.repositories.TransactionRepo;
import com.unisew.server.repositories.WalletRepo;
import com.unisew.server.requests.CreateTransactionRequest;
import com.unisew.server.requests.GetPaymentURLRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.PaymentService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CustomerRepo customerRepo;
    private final DesignRequestRepo designRequestRepo;
    private final OrderRepo orderRepo;
    private final WalletRepo walletRepo;
    private final JWTService jwtService;
    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;



    @Override
    public ResponseEntity<ResponseObject> getPaymentURL(GetPaymentURLRequest request, HttpServletRequest httpRequest) {
        return createUrl(request, httpRequest);
    }

    private ResponseEntity<ResponseObject> createUrl(GetPaymentURLRequest request, HttpServletRequest httpRequest){
        String vnp_Version = "2.1.1";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(httpRequest);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = request.getOrderType();
        String locate = "vn";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount() * 100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", request.getDescription());
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", locate);

        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl + request.getReturnURL());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        Map<String, Object> data = new HashMap<>();
        data.put("url", VNPayConfig.vnp_PayUrl + "?" + queryUrl);
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createTransaction(CreateTransactionRequest request, HttpServletRequest httpRequest) {
        String error = validateCreateTransaction(request);
        if(!error.isEmpty()){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Account sender = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if(sender == null){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Sender not found", null);
        }

        boolean isPaymentSuccess = request.getGatewayCode().equalsIgnoreCase("00");

        Account receiver = customerRepo.findById(request.getReceiverId()).get().getAccount();

        Wallet adminWallet = getAdminWallet();
        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();
        String balanceType = "pending";

        long amount = request.getTotalPrice() - request.getServiceFee();

        if(isPaymentSuccess){
            adminWallet.setPendingBalance(adminWallet.getPendingBalance() + request.getServiceFee());
            if(request.getType().equalsIgnoreCase(PaymentType.WALLET.name())){
                receiverWallet.setBalance(receiverWallet.getBalance() + amount);
                balanceType = "balance";
            }else {
                receiverWallet.setPendingBalance(receiverWallet.getPendingBalance() + amount);
            }

            walletRepo.save(adminWallet);
            receiverWallet = walletRepo.save(receiverWallet);
        }

        if(!isPaymentSuccess) balanceType = "fail";

        if(request.isPayFromWallet()){
            return payFromWallet(request, senderWallet, receiverWallet, balanceType);
        }
        return payFromGateway(request, senderWallet, receiverWallet, balanceType);
    }

    private String validateCreateTransaction(CreateTransactionRequest request){
        if(checkIfNullOrEmpty(request.getType())){
            return "Type is required";
        }

        PaymentType paymentType = Arrays.stream(PaymentType.values())
                .filter(e -> e.name().equalsIgnoreCase(request.getType()))
                .findFirst()
                .orElse(null);

        if(paymentType == null){
            return "Type invalid";
        }

        if(!customerRepo.existsById(request.getReceiverId())){
            return "Receiver not found";
        }

        if(request.getType().equalsIgnoreCase("design") && !designRequestRepo.existsById(request.getItemId())){
            return "Design request not found";
        }

        if(request.getType().equalsIgnoreCase("order") && !orderRepo.existsById(request.getItemId())){
            return "Order not found";
        }

        if(request.getTotalPrice() < 0) {
            return "Total price must be greater than 0";
        }

        if(checkIfNullOrEmpty(request.getGatewayCode())){
            return "Payment gateway is required";
        }

        if(request.getServiceFee() < 0){
            return "Service fee must be greater than 0";
        }

        return "";
    }

    private boolean checkIfNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }

    private ResponseEntity<ResponseObject> payFromWallet(CreateTransactionRequest request, Wallet senderWallet, Wallet receiverWallet, String balanceType)  {
        long amount = request.getTotalPrice() - request.getServiceFee();
        boolean isPaymentSuccess = request.getGatewayCode().equalsIgnoreCase("00");

        if(isPaymentSuccess){
            if(senderWallet.getBalance() < amount){
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Balance not enough", null);
            }

            senderWallet.setBalance(senderWallet.getBalance() - amount);
            senderWallet = walletRepo.save(senderWallet);
        }

        return createTransaction(request, senderWallet, receiverWallet, amount, balanceType);
    }

    private ResponseEntity<ResponseObject> payFromGateway(CreateTransactionRequest request, Wallet senderWallet, Wallet receiverWallet, String balanceType){
        long amount = request.getTotalPrice() - request.getServiceFee();

        return createTransaction(request, senderWallet, receiverWallet, amount, balanceType);
    }

    private ResponseEntity<ResponseObject> createTransaction(CreateTransactionRequest request, Wallet senderWallet, Wallet receiverWallet, long amount, String balanceType) {
        transactionRepo.save(
                Transaction.builder()
                        .wallet(senderWallet)
                        .sender(senderWallet.getAccount().getCustomer())
                        .receiver(receiverWallet.getAccount().getCustomer())
                        .itemId(request.getItemId())
                        .senderName(senderWallet.getAccount().getCustomer().getName())
                        .receiverName(receiverWallet.getAccount().getCustomer().getName())
                        .balanceType(balanceType)
                        .amount(amount)
                        .paymentType(PaymentType.valueOf(request.getType().toUpperCase()))
                        .serviceFee(request.getServiceFee())
                        .status(request.getGatewayCode().trim().equalsIgnoreCase("00") ? Status.TRANSACTION_SUCCESS : Status.TRANSACTION_FAIL)
                        .creationDate(LocalDate.now())
                        .paymentGatewayCode(request.getGatewayCode().trim())
                        .build()
        );

        return ResponseBuilder.build(HttpStatus.CREATED, "Transaction created", null);
    }

    private Wallet getAdminWallet(){
        return walletRepo.findByAccount_Role(Role.ADMIN);
    }
}
