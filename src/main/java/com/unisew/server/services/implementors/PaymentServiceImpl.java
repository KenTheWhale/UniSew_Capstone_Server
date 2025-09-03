package com.unisew.server.services.implementors;

import com.unisew.server.configurations.VNPayConfig;
import com.unisew.server.enums.PaymentType;
import com.unisew.server.enums.ProblemLevel;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.Feedback;
import com.unisew.server.models.Order;
import com.unisew.server.models.Transaction;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.CustomerRepo;
import com.unisew.server.repositories.DeliveryItemRepo;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.DesignQuotationRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.FeedbackRepo;
import com.unisew.server.repositories.OrderRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.TransactionRepo;
import com.unisew.server.repositories.WalletRepo;
import com.unisew.server.requests.CreateTransactionRequest;
import com.unisew.server.requests.GetPaymentURLRequest;
import com.unisew.server.requests.RefundRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.PaymentService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Predicate;

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
    private final FeedbackRepo feedbackRepo;
    private final PartnerRepo partnerRepo;
    private final DeliveryItemRepo deliveryItemRepo;
    private final DesignQuotationRepo designQuotationRepo;
    private final DesignItemRepo designItemRepo;


    @Override
    public ResponseEntity<ResponseObject> getPaymentURL(GetPaymentURLRequest request, HttpServletRequest httpRequest) {
        return createUrl(request, httpRequest);
    }

    private ResponseEntity<ResponseObject> createUrl(GetPaymentURLRequest request, HttpServletRequest httpRequest) {
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
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Account sender = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (sender == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Sender not found", null);
        }

        boolean isPaymentSuccess = request.getGatewayCode().equalsIgnoreCase("00");

        Account receiver;
        if (request.getType().equalsIgnoreCase(PaymentType.WALLET.name())) {
            receiver = sender;
        } else {
            receiver = customerRepo.findById(request.getReceiverId()).get().getAccount();
        }

        Wallet adminWallet = getAdminWallet();
        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();
        String balanceType = "pending";

        long amount = request.getTotalPrice() - request.getServiceFee();

        if (isPaymentSuccess) {
            adminWallet.setPendingBalance(adminWallet.getPendingBalance() + request.getServiceFee());
            if (request.getType().equalsIgnoreCase(PaymentType.WALLET.name())) {
                receiverWallet.setBalance(receiverWallet.getBalance() + amount);
                balanceType = "balance";
            } else {
                receiverWallet.setPendingBalance(receiverWallet.getPendingBalance() + amount);
            }

            walletRepo.save(adminWallet);
            receiverWallet = walletRepo.save(receiverWallet);

            if (request.getType().equalsIgnoreCase(PaymentType.ORDER.name()) && request.getItemId() != null) {
                orderRepo.findById(request.getItemId()).ifPresent(o -> {
                    o.setDisburseAt(Instant.now().plus(7, ChronoUnit.DAYS));
                    orderRepo.save(o);
                });
            }
        }

        if (!isPaymentSuccess) balanceType = "fail";

        if (request.isPayFromWallet()) {
            return payFromWallet(request, senderWallet, receiverWallet, balanceType);
        }
        return payFromGateway(request, senderWallet, receiverWallet, balanceType);
    }

    private String validateCreateTransaction(CreateTransactionRequest request) {
        if (checkIfNullOrEmpty(request.getType())) {
            return "Type is required";
        }

        PaymentType paymentType = Arrays.stream(PaymentType.values())
                .filter(e -> e.name().equalsIgnoreCase(request.getType()))
                .findFirst()
                .orElse(null);

        if (paymentType == null) {
            return "Type invalid";
        }

        if (!request.getType().equalsIgnoreCase(PaymentType.WALLET.getValue()) && !customerRepo.existsById(request.getReceiverId())) {
            return "Receiver not found";
        }

        DesignRequest designRequest = designRequestRepo.findById(request.getItemId()).orElse(null);
        if (request.getType().equalsIgnoreCase("design") && designRequest == null) {
            return "Design request not found";
        }

        if (request.getType().equalsIgnoreCase("order") && !orderRepo.existsById(request.getItemId())) {
            return "Order not found";
        }

        if (request.getTotalPrice() < 0) {
            return "Total price must be greater than 0";
        }

        if (checkIfNullOrEmpty(request.getGatewayCode())) {
            return "Payment gateway is required";
        }

        if (request.getServiceFee() < 0) {
            return "Service fee must be greater than 0";
        }

        return "";
    }

    private boolean checkIfNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private ResponseEntity<ResponseObject> payFromWallet(CreateTransactionRequest request, Wallet senderWallet, Wallet receiverWallet, String balanceType) {
        long amount = request.getTotalPrice() - request.getServiceFee();
        if(request.getType().equalsIgnoreCase(PaymentType.DEPOSIT.name())){
            amount = request.getTotalPrice();
        }
        boolean isPaymentSuccess = request.getGatewayCode().equalsIgnoreCase("00");

        if (isPaymentSuccess) {
            if (senderWallet.getBalance() < request.getTotalPrice()) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Balance not enough", null);
            }

            senderWallet.setBalance(senderWallet.getBalance() - request.getTotalPrice());
            senderWallet = walletRepo.save(senderWallet);
        }

        return createTransaction(request, senderWallet, receiverWallet, amount, balanceType);
    }

    private ResponseEntity<ResponseObject> payFromGateway(CreateTransactionRequest request, Wallet senderWallet, Wallet receiverWallet, String balanceType) {
        long amount = request.getTotalPrice() - request.getServiceFee();
        if(request.getType().equalsIgnoreCase(PaymentType.DEPOSIT.name())){
            amount = request.getTotalPrice();
        }

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

    private Wallet getAdminWallet() {
        return walletRepo.findByAccount_Role(Role.ADMIN);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllTransaction() {
        List<Transaction> transactions = transactionRepo.findAllByOrderByIdDesc();
        return ResponseBuilder.build(HttpStatus.OK, "Transactions", EntityResponseBuilder.buildListTransactionResponse(transactions));
    }

    @Override
    public ResponseEntity<ResponseObject> getTransactions(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }
        List<Transaction> transactions = transactionRepo.findAllBySender_IdOrReceiver_Id(account.getCustomer().getId(), account.getCustomer().getId()).stream()
                .sorted((t1, t2) -> Integer.compare(t2.getId(), t1.getId()))
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Transactions get successfully", EntityResponseBuilder.buildListTransactionResponse(transactions));
    }

    @Override
    public ResponseEntity<ResponseObject> getMyTransaction(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }

        Integer cid = account.getCustomer().getId();


        List<Transaction> transactions = transactionRepo
                .findAllBySender_IdOrReceiver_Id(cid, cid)
                .stream()
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                .toList();


        Map<String, Map<Integer, List<Transaction>>> buckets = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            String kind = groupNameOf(t.getPaymentType());
            Integer itemKey = (t.getItemId() != null) ? t.getItemId() : -1;

            buckets.computeIfAbsent(kind, k -> new LinkedHashMap<>());

            buckets.get(kind).computeIfAbsent(itemKey, k -> new ArrayList<>());

            buckets.get(kind).get(itemKey).add(t);
        }


        List<Map<String, Object>> groups = new ArrayList<>();

        for (Map.Entry<String, Map<Integer, List<Transaction>>> kindEntry : buckets.entrySet()) {
            String kind = kindEntry.getKey();
            Map<Integer, List<Transaction>> byItem = kindEntry.getValue();

            for (Map.Entry<Integer, List<Transaction>> itemEntry : byItem.entrySet()) {
                Integer itemId = itemEntry.getKey();
                List<Transaction> txsOfThisItem = itemEntry.getValue();

                Map<String, Object> group = new LinkedHashMap<>();
                group.put("kind", kind);
                group.put("itemId", itemId);

                if ("order".equalsIgnoreCase(kind) && itemId != null && itemId > 0) {
                    Order order = orderRepo.findById(itemId).orElse(null);
                    if (order != null) {
                        group.put("order", EntityResponseBuilder.buildOrder(
                                order, partnerRepo, deliveryItemRepo, designItemRepo, designQuotationRepo, designRequestRepo, transactionRepo
                        ));
                    } else {
                        group.put("title", "Order #" + itemId);
                    }
                } else if ("design".equalsIgnoreCase(kind) && itemId != null && itemId > 0) {
                    DesignRequest dr = designRequestRepo.findById(itemId).orElse(null);
                    if (dr != null) {
                        group.put("design", EntityResponseBuilder.buildDesignRequestResponse(dr));
                    } else {
                        group.put("title", "Design #" + itemId);
                    }
                } else {
                    group.put("title", "Wallet");
                }

                List<Map<String, Object>> txMaps = EntityResponseBuilder.buildListTransactionResponse(txsOfThisItem);
                group.put("transactions", txMaps);

                groups.add(group);
            }
        }

        return ResponseBuilder.build(HttpStatus.OK, "Transaction grouped", groups);
    }

    private String groupNameOf(PaymentType pt) {
        if (pt == null) return "wallet";
        return switch (pt) {
            case ORDER, ORDER_RETURN, DEPOSIT -> "order";
            case DESIGN, DESIGN_RETURN -> "design";
            default -> "wallet";
        };
    }

    private String buildTitle(String kind, Integer itemId) {
        if ("order".equals(kind))  return "Order #"  + (itemId > 0 ? itemId : "");
        if ("design".equals(kind)) return "Design #" + (itemId > 0 ? itemId : "");
        return "Wallet";
    }


    @Override
    @Transactional
    public ResponseEntity<ResponseObject> refundTransaction(RefundRequest request, HttpServletRequest httpRequest) {
        Account actor = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (actor == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }

        String error = validateRefund(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Feedback report = feedbackRepo.findById(request.getReportId()).orElse(null);
        if (report == null || !report.isReport()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Report not found", null);
        }

        List<Transaction> transactions;
        if (report.getDesignRequest() != null) {

            transactions = transactionRepo.findAllByItemId(report.getDesignRequest().getId()).stream()
                    .filter(transaction -> transaction.getPaymentType().equals(PaymentType.DESIGN) && transaction.getBalanceType().equals("pending"))
                    .toList();
        } else {
            transactions = transactionRepo.findAllByItemId(report.getOrder().getId()).stream()
                    .filter(transaction -> (transaction.getPaymentType().equals(PaymentType.ORDER) || transaction.getPaymentType().equals(PaymentType.DEPOSIT)) && transaction.getBalanceType().equals("pending"))
                    .toList();
        }

        boolean isMatching = feedbackMatchesDecision(transactions.get(0), "ACCEPTED".equalsIgnoreCase(request.getDecision()));
        if (!isMatching) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Report status does not match the decision", null);
        }

        long totalAmount = 0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        long refundAmount = 0;

        if (request.getProblemLevel().equals(ProblemLevel.LOW.getValue())) {
            refundAmount = (long) Math.floor(totalAmount * 0.25);
        } else if (request.getProblemLevel().equals(ProblemLevel.MEDIUM.getValue())) {
            refundAmount = (long) Math.floor(totalAmount * 0.50);
        } else if (request.getProblemLevel().equals(ProblemLevel.HIGH.getValue())) {
            refundAmount = (long) Math.floor(totalAmount * 0.75);
        } else if (request.getProblemLevel().equals(ProblemLevel.SERIOUS.getValue())) {
            refundAmount = totalAmount;
        }

        Wallet partnerWallet = transactions.get(0).getReceiver().getAccount().getWallet();
        Wallet schoolWallet = transactions.get(0).getSender().getAccount().getWallet();
        partnerWallet.setPendingBalance(partnerWallet.getPendingBalance() - refundAmount);
        schoolWallet.setPendingBalance(schoolWallet.getPendingBalance() + refundAmount);

        walletRepo.save(partnerWallet);
        walletRepo.save(schoolWallet);

        transactionRepo.save(
                Transaction.builder()
                        .wallet(schoolWallet)
                        .sender(partnerWallet.getAccount().getCustomer())
                        .receiver(schoolWallet.getAccount().getCustomer())
                        .itemId(transactions.get(0).getItemId())
                        .senderName(partnerWallet.getAccount().getCustomer().getName())
                        .receiverName(schoolWallet.getAccount().getCustomer().getName())
                        .balanceType("balance")
                        .amount(refundAmount)
                        .paymentType(transactions.get(0).getPaymentType() == PaymentType.DESIGN ? PaymentType.DESIGN_RETURN : PaymentType.ORDER_RETURN)
                        .serviceFee(0)
                        .status(Status.TRANSACTION_SUCCESS)
                        .creationDate(LocalDate.now())
                        .paymentGatewayCode("00")
                        .build()
        );

        return ResponseBuilder.build(HttpStatus.OK, "Refund successfully", null);
    }

    private String validateRefund(RefundRequest request) {
        if (request == null) return "Request is required";
        if (request.getDecision() == null || request.getDecision().isBlank()) return "decision is required";
        if (!"ACCEPTED".equalsIgnoreCase(request.getDecision()) && !"REJECTED".equalsIgnoreCase(request.getDecision())) {
            return "decision must be ACCEPTED or REJECTED";
        }
        return null;
    }

    private boolean feedbackMatchesDecision(Transaction tx, boolean approved) {
        if (tx == null || tx.getItemId() == null) return false;
        if (tx.getReceiver() == null || tx.getReceiver().getAccount() == null) return false;

        Role receiverRole = tx.getReceiver().getAccount().getRole();
        if (receiverRole == null) return false;

        Predicate<Feedback> matches = fb -> {
            if (fb == null || !fb.isReport()) return false;
            return approved
                    ? fb.getStatus() == Status.FEEDBACK_REPORT_RESOLVED_ACCEPTED
                    : fb.getStatus() == Status.FEEDBACK_REPORT_RESOLVED_REJECTED;
        };

        if (receiverRole == Role.DESIGNER) {
            DesignRequest dr = designRequestRepo.findById(tx.getItemId()).orElse(null);
            return dr != null && matches.test(dr.getFeedback());
        }

        if (receiverRole == Role.GARMENT) {
            Order od = orderRepo.findById(tx.getItemId()).orElse(null);
            return od != null && matches.test(od.getFeedback());
        }

        return false;
    }

    @Override
    public ResponseEntity<ResponseObject> getWalletBalance(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if(account == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);

        Map<String, Object> data = new HashMap<>();
        data.put("balance", account.getWallet().getBalance());

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    //    public ResponseEntity<ResponseObject> disburseTransaction(RefundRequest request, HttpServletRequest httpRequest) {
//        Account actor = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
//        if (actor == null || actor.getRole() != Role.ADMIN) {
//            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Only ADMIN can refund/resolve report", null);
//        }
//
//        String error = validateRefund(request);
//        if (error != null) {
//            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
//        }
//
//        Feedback report = feedbackRepo.findById(request.getReportId()).orElse(null);
//        if (report == null || !report.isReport()) {
//            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Report not found", null);
//        }
//
//        List<Transaction> transactions;
//        if (report.getDesignRequest() != null) {
//
//            transactions = transactionRepo.findAllByItemId(report.getDesignRequest().getId()).stream()
//                    .filter(transaction -> transaction.getPaymentType().equals(PaymentType.DESIGN) && !transaction.getBalanceType().equals("fail"))
//                    .toList();
//        } else {
//            transactions = transactionRepo.findAllByItemId(report.getOrder().getId()).stream()
//                    .filter(transaction -> (transaction.getPaymentType().equals(PaymentType.ORDER) || transaction.getPaymentType().equals(PaymentType.DEPOSIT)) && !transaction.getBalanceType().equals("fail"))
//                    .toList();
//        }
//
//        List<Transaction> balanceTransactions = new ArrayList<>();
//        List<Transaction> pendingTransactions = new ArrayList<>();
//        for (Transaction transaction : transactions) {
//            if (transaction.getBalanceType().equals("pending")) {
//                pendingTransactions.add(transaction);
//            } else {
//                balanceTransactions.add(transaction);
//            }
//        }
//        int variance = pendingTransactions.size() - balanceTransactions.size(); //do lech
//
//        List<Transaction> refundTransaction = new ArrayList<>();
//        for (int i = variance; i > 0; i++) {
//            refundTransaction.add(pendingTransactions.get(pendingTransactions.size() - i));
//        }
//
//    }


}
