package com.unisew.server.services.implementors;

import com.unisew.server.enums.DeliveryItemSize;
import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.OrderService;
import com.unisew.server.services.PaymentService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
import com.unisew.server.utils.MapUtils;
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
import java.util.Arrays;
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
    DesignDeliveryRepo designDeliveryRepo;
    DesignItemRepo designItemRepo;
    DeliveryItemRepo deliveryItemRepo;
    SewingPhaseRepo sewingPhaseRepo;
    MilestoneRepo milestoneRepo;
    PaymentService paymentService;


    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request) {
        String error = OrderValidation.validate(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        DesignDelivery delivery = designDeliveryRepo.findById(request.getDeliveryId()).orElse(null);
        if (delivery == null || delivery.getSchoolDesign() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        SchoolDesign schoolDesign = delivery.getSchoolDesign();


        Order order = orderRepo.save(
                Order.builder()
                        .schoolDesign(schoolDesign)
                        .feedback(null)
                        .garmentId(null)
                        .garmentName("")
                        .deadline(request.getDeadline())
                        .price(0)
                        .serviceFee(0)
                        .orderDate(LocalDate.now())
                        .note(request.getNote())
                        .status(Status.ORDER_PENDING)
                        .build()
        );

        List<OrderDetail> orderDetailEntities = new ArrayList<>();
        if (request.getOrderDetails() != null) {
            for (CreateOrderRequest.OrderItem item : request.getOrderDetails()) {
                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .deliveryItemId(item.getDeliveryItemId())
                        .size(DeliveryItemSize.valueOf(item.getSize()))
                        .quantity(item.getQuantity())
                        .build();
                orderDetailEntities.add(detail);
            }
            orderDetailRepo.saveAll(orderDetailEntities);
        }

        order.setOrderDetails(orderDetailEntities);
        orderRepo.save(order);

        return ResponseBuilder.build(HttpStatus.CREATED, "Order created successfully!", null);
    }

    @Override
    public ResponseEntity<ResponseObject> viewAllOrder(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);
        }
//        List<Order> orders = orderRepo.findAll().stream()
//                .filter(order -> order.getGarmentId() == null)
//                .toList();
        List<Order> orders = orderRepo.findAll();
        return ResponseBuilder.build(HttpStatus.OK, "", EntityResponseBuilder.buildOrderList(orders, partnerRepo, deliveryItemRepo, designItemRepo, sewingPhaseRepo));
    }

    @Override
    public ResponseEntity<ResponseObject> viewSchoolOrder(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);

        List<Order> orders = account.getCustomer().getSchoolDesigns()
                .stream()
                .filter(schoolDesign -> schoolDesign.getOrders() != null && !schoolDesign.getOrders().isEmpty())
                .map(SchoolDesign::getOrders)
                .flatMap(List::stream)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "", EntityResponseBuilder.buildOrderList(orders, partnerRepo, deliveryItemRepo, designItemRepo, sewingPhaseRepo));
    }

    @Override
    public ResponseEntity<ResponseObject> createSewingPhase(HttpServletRequest httpServletRequest, CreateSewingPhaseRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }
        String error = OrderValidation.validateCreateSewingPhase(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }
        Partner partner = partnerRepo.findById(account.getCustomer().getPartner().getId()).orElse(null);
        if (partner == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Garment not found", null);
        }
        sewingPhaseRepo.save(
                SewingPhase.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .status(Status.SEWING_PHASE_ACTIVE)
                        .garment(partner)
                        .build()
        );
        return ResponseBuilder.build(HttpStatus.CREATED, "Sewing phase created successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> assignMilestone(HttpServletRequest httpServletRequest, AssignMilestoneRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }
        String error = OrderValidation.validateAssignMilestone(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }
        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        for (AssignMilestoneRequest.Phase phase : request.getPhaseList()) {
            SewingPhase sewingPhase = sewingPhaseRepo.findById(phase.getId()).orElse(null);
            if (sewingPhase == null) {
                return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Sewing phase not found", null);
            }

            milestoneRepo.save(Milestone.builder()
                    .stage(phase.getStage())
                    .startDate(phase.getStartDate())
                    .endDate(phase.getEndDate())
                    .status(Status.MILESTONE_ASSIGNED)
                    .phase(sewingPhase)
                    .order(order)
                    .build());
        }

        return ResponseBuilder.build(HttpStatus.OK, "Milestone assigned successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateMilestoneStatus(UpdateMilestoneStatusRequest request) {
        Milestone milestone = milestoneRepo.findById(request.getMilestoneId()).orElse(null);
        if (milestone == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Milestone not found", null);
        }

        if (milestone.getEndDate() != null && LocalDate.now().isAfter(milestone.getEndDate())) {
            milestone.setStatus(Status.MILESTONE_LATE);
        } else {
            switch (milestone.getStatus()) {
                case MILESTONE_ASSIGNED:
                    milestone.setStatus(Status.MILESTONE_PROCESSING);
                    break;
                case MILESTONE_PROCESSING:
                    milestone.setStatus(Status.MILESTONE_COMPLETED);
                    break;
                default:
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            milestone.setImgUrl(request.getImageUrl());
        }

        milestoneRepo.save(milestone);

        Order order = milestone.getOrder();
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found for the milestone", null);
        }

        List<Milestone> milestones = milestoneRepo.findAllByPhase_Id(milestone.getPhase().getId());

        boolean isHighestStage = milestones.stream()
                .allMatch(m -> m.getStage() <= milestone.getStage());

        if (isHighestStage) {
            order.setStatus(Status.ORDER_COMPLETED);
        }

        orderRepo.save(order);

        return ResponseBuilder.build(HttpStatus.OK, "Milestone status updated successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> viewMilestone(int orderId) {
        List<Milestone> milestones = milestoneRepo.findAllByOrder_Id(orderId).stream()
                .sorted((m1, m2) -> Integer.compare(m1.getStage(), m2.getStage()))
                .toList();
        if (milestones.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Milestone not found", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Milestone view successfully", EntityResponseBuilder.buildOrderMilestoneList(milestones));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpServletRequest, QuotationRequest request) {
        String error = QuotationValidation.validate(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
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
    public ResponseEntity<ResponseObject> approveQuotation(ApproveQuotationRequest request, HttpServletRequest httpServletRequest) {
        GarmentQuotation garmentQuotation = garmentQuotationRepo.findById(request.getQuotationId()).orElse(null);
        String error = ApproveQuotationValidation.validate(garmentQuotation);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        garmentQuotation.setStatus(Status.GARMENT_QUOTATION_APPROVED);
        garmentQuotationRepo.save(garmentQuotation);

        List<GarmentQuotation> otherGarmentQuotations = garmentQuotationRepo.findAllByOrder_Id(garmentQuotation.getOrder().getId());
        for (GarmentQuotation item : otherGarmentQuotations) {
            if (!item.getId().equals(request.getQuotationId()) && item.getStatus() == Status.GARMENT_QUOTATION_PENDING) {
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
        order.setNote(order.getNote());
        orderRepo.save(order);

        return paymentService.createTransaction(request.getCreateTransactionRequest(), httpServletRequest);
    }

    @Override
    public ResponseEntity<ResponseObject> viewQuotation(int orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "List of quotations", EntityResponseBuilder.buildQuotationResponse(order.getGarmentQuotations()));
    }

    @Override
    public ResponseEntity<ResponseObject> getSizes() {
        List<Map<String, Object>> data = Arrays.stream(DeliveryItemSize.values())
                .map(this::buildSize)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    private Map<String, Object> buildSize(DeliveryItemSize size) {
        List<String> keys = List.of(
                "type", "size", "gender",
                "maxHeight", "minHeight",
                "maxWeight", "minWeight",
                "enumName"
        );
        List<Object> values = List.of(
                size.getType(), size.getSize(), size.getGender(),
                size.getMaxHeight(), size.getMinHeight(),
                size.getMaxWeight(), size.getMinWeight(),
                size.name()
        );
        return MapUtils.build(keys, values);
    }

    @Override
    public ResponseEntity<ResponseObject> cancelOrder(int orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }
        if (order.getStatus() != Status.ORDER_PENDING) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Order cannot be canceled at this stage", null);
        }
        order.setStatus(Status.ORDER_CANCELED);
        orderRepo.save(order);
        return ResponseBuilder.build(HttpStatus.OK, "Order canceled successfully", null);
    }


}
