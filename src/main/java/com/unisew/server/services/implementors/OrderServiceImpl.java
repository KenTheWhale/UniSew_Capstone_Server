package com.unisew.server.services.implementors;

import com.unisew.server.enums.DesignItemSize;
import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.QuotationRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.OrderService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.ResponseBuilder;
import com.unisew.server.validations.ApproveQuotationValidation;
import com.unisew.server.validations.OrderValidation;
import com.unisew.server.validations.QuotationValidation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepo orderRepo;
    PartnerRepo partnerRepo;
    SchoolDesignRepo schoolDesignRepo;
    OrderDetailRepo orderDetailRepo;
    GarmentQuotationRepo garmentQuotationRepo;
    JWTService jwtService;
    AccountRepo accountRepo;

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request) {
        String error = OrderValidation.validate(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.OK, error, null);
        }

        SchoolDesign schoolDesign = schoolDesignRepo.findByDesignDelivery_Id(request.getDeliveryId())
                .orElse(null);
        if (schoolDesign == null) {
            return ResponseBuilder.build(HttpStatus.OK, "School Design not found", null);
        }

        Partner garment = partnerRepo.findById(request.getGarmentId())
                .orElse(null);
        if (garment == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Garment not found", null);
        }

        Order order = Order.builder()
                .schoolDesign(schoolDesign)
                .deadline(request.getDeadline())
                .price(0)
                .serviceFee(0)
                .orderDate(LocalDate.now())
                .note(request.getNote())
                .status(Status.ORDER_PENDING)
                .build();

        order = orderRepo.save(order);

        List<OrderDetail> orderDetailEntities = new ArrayList<>();
        if (request.getOrderDetails() != null) {
            for (CreateOrderRequest.OrderItem item : request.getOrderDetails()) {
                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .deliveryItemId(item.getDeliveryItemId())
                        .size(DesignItemSize.valueOf(item.getSize()))
                        .quantity(item.getQuantity())
                        .build();
                orderDetailEntities.add(detail);
            }
            orderDetailRepo.saveAll(orderDetailEntities);
        }

        order.setOrderDetails(orderDetailEntities);

        orderRepo.save(Order.builder()

                .build()
        );

        //Tạo Transaction

        return ResponseBuilder.build(HttpStatus.OK, "Order created successfully!", null);
    }

    @Override
    public ResponseEntity<ResponseObject> viewOrder() {
        List<Order> orders = orderRepo.findAll();
        if (orders.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No orders found", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Orders found", buildOrder(orders));
    }

    private List<Map<String, Object>> buildOrder(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus().equals(Status.ORDER_PENDING))
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("schoolName", order.getSchoolDesign().getCustomer().getName());
                    orderMap.put("garmentId", order.getGarmentId());
                    orderMap.put("garmentName", order.getGarmentName());
                    orderMap.put("deadline", order.getDeadline());
                    orderMap.put("price", order.getPrice());
                    orderMap.put("serviceFee", order.getServiceFee());
                    orderMap.put("orderDate", order.getOrderDate());
                    orderMap.put("note", order.getNote());
                    orderMap.put("status", order.getStatus().name());
                    orderMap.put("orderDetails", buildOrderDetail(order.getOrderDetails()));
                    return orderMap;
                })
                .toList();
    }

    private List<Map<String, Object>> buildOrderDetail(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(orderDetail -> {
                            Map<String, Object> detailMap = new HashMap<>();
                            detailMap.put("id", orderDetail.getId());
                            detailMap.put("deliveryItemId", orderDetail.getDeliveryItemId());
                            detailMap.put("size", orderDetail.getSize().name());
                            detailMap.put("quantity", orderDetail.getQuantity());
                            return detailMap;
                        }
                )
                .toList();
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, QuotationRequest request) {
        String error = QuotationValidation.validate(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.OK, error, null);
        }

        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Order not found", null);
        }

        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        GarmentQuotation garmentQuotation = GarmentQuotation.builder()
                .order(order)
                .garment(account.getCustomer().getPartner())
                .earlyDeliveryDate(request.getEarlyDeliveryDate())
                .acceptanceDeadline(request.getAcceptanceDeadline())
                .price(request.getPrice())
                .note(request.getNote())
                .status(Status.GARMENT_QUOTATION_PENDING)
                .build();

        garmentQuotationRepo.save(garmentQuotation);

        return ResponseBuilder.build(HttpStatus.OK, "Quotation created successfully!", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> approveQuotation(int quotationId) {
        GarmentQuotation garmentQuotation = garmentQuotationRepo.findById(quotationId).orElse(null);
        String error = ApproveQuotationValidation.validate(garmentQuotation);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        garmentQuotation.setStatus(Status.GARMENT_QUOTATION_APPROVED);
        garmentQuotationRepo.save(garmentQuotation);

        List<GarmentQuotation> otherGarmentQuotations = garmentQuotationRepo.findAllByOrder_Id(garmentQuotation.getOrder().getId());
        for (GarmentQuotation item : otherGarmentQuotations) {
            if (!item.getId().equals(quotationId) && item.getStatus() == Status.GARMENT_QUOTATION_PENDING) {
                item.setStatus(Status.GARMENT_QUOTATION_REJECTED);
                garmentQuotationRepo.save(item);
            }
        }

        Order order = garmentQuotation.getOrder();
        order.setStatus(Status.ORDER_PROCESSING);
        order.setGarmentId(garmentQuotation.getGarment().getId());
        order.setGarmentName(garmentQuotation.getGarment().getCustomer().getName());
        order.setPrice(garmentQuotation.getPrice());
        order.setServiceFee(garmentQuotation.getPrice() * 5 / 100);
        order.setNote(order.getNote() + "&&" + garmentQuotation.getNote());
        orderRepo.save(order);

        return ResponseBuilder.build(HttpStatus.OK, "Quotation approved successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> viewQuotation(int orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        List<GarmentQuotation> garmentQuotations = order.getGarmentQuotations();

        List<Map<String, Object>> data = (garmentQuotations != null) ? garmentQuotations.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("garmentId", item.getGarment().getId());
            map.put("garmentName", item.getGarment().getCustomer().getName());
            map.put("earlyDeliveryDate", item.getEarlyDeliveryDate());
            map.put("acceptanceDeadline", item.getAcceptanceDeadline());
            map.put("price", item.getPrice());
            map.put("note", item.getNote());
            map.put("status", item.getStatus());
            return map;
        }).toList()
                : new ArrayList<>();

        return ResponseBuilder.build(HttpStatus.OK, "List of quotations", data);
    }


}
