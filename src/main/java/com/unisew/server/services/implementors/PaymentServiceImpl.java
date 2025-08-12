package com.unisew.server.services.implementors;

import com.unisew.server.configurations.VNPayConfig;
import com.unisew.server.enums.PaymentType;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Customer;
import com.unisew.server.models.Transaction;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.*;
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
}
