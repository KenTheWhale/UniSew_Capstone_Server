package com.unisew.server.services.implementors;

import com.unisew.server.controllers.ConfirmOrderRequest;
import com.unisew.server.enums.DeliveryItemSize;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.DesignDelivery;
import com.unisew.server.models.GarmentQuotation;
import com.unisew.server.models.Milestone;
import com.unisew.server.models.Order;
import com.unisew.server.models.OrderDetail;
import com.unisew.server.models.Partner;
import com.unisew.server.models.SchoolDesign;
import com.unisew.server.models.SewingPhase;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.DeliveryItemRepo;
import com.unisew.server.repositories.DesignDeliveryRepo;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.DesignQuotationRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.GarmentQuotationRepo;
import com.unisew.server.repositories.MilestoneRepo;
import com.unisew.server.repositories.OrderDetailRepo;
import com.unisew.server.repositories.OrderRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.SewingPhaseRepo;
import com.unisew.server.repositories.TransactionRepo;
import com.unisew.server.requests.ApproveQuotationRequest;
import com.unisew.server.requests.AssignMilestoneRequest;
import com.unisew.server.requests.CancelOrderRequest;
import com.unisew.server.requests.ConfirmDeliveredOrderRequest;
import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.CreateSewingPhaseRequest;
import com.unisew.server.requests.QuotationRequest;
import com.unisew.server.requests.UpdateMilestoneStatusRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.OrderService;
import com.unisew.server.services.PaymentService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepo orderRepo;
    PartnerRepo partnerRepo;
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
    DesignRequestRepo designRequestRepo;
    DesignQuotationRepo designQuotationRepo;
    private final TransactionRepo transactionRepo;


    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createOrder(HttpServletRequest httpServletRequest, CreateOrderRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null || account.getCustomer() == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account/Customer not found", null);
        }

        String error = OrderValidation.validate(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        DesignDelivery delivery = designDeliveryRepo.findById(request.getDeliveryId()).orElse(null);
        if (delivery == null || delivery.getSchoolDesign() == null || delivery.getSchoolDesign().getCustomer() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        SchoolDesign schoolDesign = delivery.getSchoolDesign();
        Integer ownerSchoolId = schoolDesign.getCustomer().getId();
        Integer callerSchoolId = account.getCustomer().getId();

        if (!Objects.equals(ownerSchoolId, callerSchoolId)) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not allowed to create order!", null);
        }

        int expectedOrderDetailsSize = delivery.getDeliveryItems().size();
        List<Integer> filteredOrderItemID = new ArrayList<>();
        request.getOrderDetails().forEach(orderItem -> {
            if (!filteredOrderItemID.contains(orderItem.getDeliveryItemId())) {
                filteredOrderItemID.add(orderItem.getDeliveryItemId());
            }
        });

        if (filteredOrderItemID.size() != expectedOrderDetailsSize) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Order details size does not match delivery items size!", null);
        }

        Order order = orderRepo.save(Order.builder()
                .schoolDesign(schoolDesign)
                .feedback(null).garmentId(null)
                .garmentName("").deadline(request.getDeadline())
                .price(0)
                .orderDate(LocalDate.now())
                .note(request.getNote())
                .status(Status.ORDER_PENDING).build());

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
    public ResponseEntity<ResponseObject> viewAllOrder() {

        List<Order> orders = orderRepo.findAll().stream().filter(order -> order.getStatus().equals(Status.ORDER_PENDING)).toList();
        return ResponseBuilder.build(HttpStatus.OK, "", EntityResponseBuilder.buildOrderList(orders, partnerRepo, deliveryItemRepo, designItemRepo, designRequestRepo, designQuotationRepo, transactionRepo));
    }

    @Override
    public ResponseEntity<ResponseObject> viewGarmentOrder(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);
        }
        List<Order> orders = orderRepo.findAllByGarmentId(account.getCustomer().getPartner().getId());
        return ResponseBuilder.build(HttpStatus.OK, "Get garment order list successfully", EntityResponseBuilder.buildOrderList(orders, partnerRepo, deliveryItemRepo, designItemRepo, designRequestRepo, designQuotationRepo, transactionRepo));
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
                .peek(order -> {
                    if (!LocalDate.now().isBefore(order.getDeadline()) && order.getStatus().equals(Status.ORDER_PENDING)) {
                        order.setStatus(Status.ORDER_CANCELED);
                        orderRepo.save(order);
                    }
                })
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "", EntityResponseBuilder.buildOrderList(orders, partnerRepo, deliveryItemRepo, designItemRepo, designRequestRepo, designQuotationRepo, transactionRepo));
    }

    @Override
    @Transactional
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

        long activePhasesCount = sewingPhaseRepo.countSewingPhaseByGarment_IdAndStatus(partner.getId(), Status.SEWING_PHASE_ACTIVE);
        if (activePhasesCount == 10) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Maximum sewing phases reached!", null);
        }
        sewingPhaseRepo.save(SewingPhase.builder().name(request.getName()).description(request.getDescription()).status(Status.SEWING_PHASE_ACTIVE).garment(partner).build());
        return ResponseBuilder.build(HttpStatus.CREATED, "Sewing phase created successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> assignMilestone(HttpServletRequest httpServletRequest, AssignMilestoneRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }
        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }
        Partner currentGarment = account.getCustomer().getPartner();
        if (order.getGarmentId() == null || !order.getGarmentId().equals(currentGarment.getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not allowed to assign milestones for an order that does not belong to your garment", null);
        }
        String error = OrderValidation.validateAssignMilestone(order, request, sewingPhaseRepo);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
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
                    .status(phase.getStartDate().isAfter(LocalDate.now()) ? Status.MILESTONE_ASSIGNED : Status.MILESTONE_PROCESSING)
                    .phase(sewingPhase)
                    .completedDate(null)
                    .order(order)
                    .build());
        }

        return ResponseBuilder.build(HttpStatus.OK, "Milestone assigned successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateMilestoneStatus(HttpServletRequest httpServletRequest, UpdateMilestoneStatusRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }
        // Update current milestone
        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        Milestone milestone = milestoneRepo.findByOrder_IdAndStatus(order.getId(), Status.MILESTONE_PROCESSING).orElse(null);
        if (milestone == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Milestone has been completed", null);
        }
        order = milestone.getOrder();
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found for the milestone", null);
        }
        if (order.getGarmentId() == null || !order.getGarmentId().equals(account.getCustomer().getPartner().getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not allowed to update a milestone for an order that does not belong to your garment", null);
        }

        if (milestone.getEndDate() != null && LocalDate.now().isAfter(milestone.getEndDate())) {
            milestone.setStatus(Status.MILESTONE_LATE);
        } else {
            milestone.setStatus(Status.MILESTONE_COMPLETED);
        }

        milestone.setCompletedDate(LocalDate.now());

        if (request.getVideoUrl() != null) {
            milestone.setVideoUrl(request.getVideoUrl());
        }

        milestone = milestoneRepo.save(milestone);

        //Update the next milestone
        //Check not final stage
        if (order.getMilestones().size() > milestone.getStage()) {
            int nextStage = milestone.getStage() + 1;
            Milestone nextMilestone = milestoneRepo.findByOrder_IdAndStage(order.getId(), nextStage).orElse(null);

            if (nextMilestone != null) {
                nextMilestone.setStatus(Status.MILESTONE_PROCESSING);
                milestoneRepo.save(nextMilestone);
            }
        }

        return ResponseBuilder.build(HttpStatus.OK, "Milestone status updated successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> viewMilestone(HttpServletRequest httpServletRequest, int orderId) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }
        if (order.getGarmentId() == null || !order.getGarmentId().equals(account.getCustomer().getPartner().getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not allowed to view milestones for an order that does not belong to your garment", null);
        }
        List<Milestone> milestones = milestoneRepo.findAllByOrder_Id(orderId).stream()
                .sorted(Comparator.comparingInt(Milestone::getStage))
                .toList();
        if (milestones.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.OK, "Milestone not found", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Milestone view successfully", EntityResponseBuilder.buildOrderMilestoneList(milestones));
    }

    @Override
    public ResponseEntity<ResponseObject> viewPhase(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }

        List<SewingPhase> phases = sewingPhaseRepo.findAll().stream().filter(phase -> phase.getStatus() == Status.SEWING_PHASE_ACTIVE).filter(phase -> phase.getGarment() != null && phase.getGarment().getId().equals(account.getCustomer().getPartner().getId())).toList();
        if (phases.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.OK, "No active sewing phases found", null);
        }
        return ResponseBuilder.build(HttpStatus.OK, "Sewing phases retrieved successfully", EntityResponseBuilder.buildSewingPhaseList(phases));
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

        if (account == null || !account.getRole().equals(Role.GARMENT)) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }

        if (garmentQuotationRepo.existsByOrder_IdAndGarment_IdAndStatus(order.getId(), account.getCustomer().getPartner().getId(), Status.GARMENT_QUOTATION_PENDING)) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "You already create a quotation for this order", null);
        }

        GarmentQuotation garmentQuotation = GarmentQuotation.builder().order(order).garment(account.getCustomer().getPartner()).earlyDeliveryDate(request.getEarlyDeliveryDate()).depositRate(request.getDepositRate()).acceptanceDeadline(request.getAcceptanceDeadline()).price(request.getPrice()).note(request.getNote()).status(Status.GARMENT_QUOTATION_PENDING).build();

        garmentQuotationRepo.save(garmentQuotation);

        return ResponseBuilder.build(HttpStatus.OK, "Quotation created successfully!", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> approveQuotation(ApproveQuotationRequest request, HttpServletRequest httpServletRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
        }
        GarmentQuotation garmentQuotation = garmentQuotationRepo.findById(request.getQuotationId()).orElse(null);
        String error = ApproveQuotationValidation.validate(garmentQuotation);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }
        if (!garmentQuotation.getOrder().getSchoolDesign().getCustomer().getId().equals(account.getCustomer().getId())) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "You are not authorized to approve this quotation", null);
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
        order.setGarmentQuotationId(garmentQuotation.getId());
        order.setStatus(Status.ORDER_PROCESSING);
        order.setGarmentId(garmentQuotation.getGarment().getId());
        order.setGarmentName(garmentQuotation.getGarment().getCustomer().getName());
        order.setPrice(garmentQuotation.getPrice());
        order.setNote(order.getNote());
        order.setDisburseAt(Instant.now().plus(7, ChronoUnit.DAYS));
        orderRepo.save(order);

        request.getCreateTransactionRequest().setReceiverId(garmentQuotation.getGarment().getCustomer().getId());
        return paymentService.createTransaction(request.getCreateTransactionRequest(), httpServletRequest);
    }

    @Override
    public ResponseEntity<ResponseObject> viewQuotation(int orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "List of quotations", EntityResponseBuilder.buildQuotationResponse(order.getGarmentQuotations(), designRequestRepo, designQuotationRepo));
    }

    @Override
    public ResponseEntity<ResponseObject> getSizes() {
        List<Map<String, Object>> data = Arrays.stream(DeliveryItemSize.values()).map(this::buildSize).toList();

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    private Map<String, Object> buildSize(DeliveryItemSize size) {
        if (size == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("type", size.getType());
        data.put("size", size.getSize());
        data.put("gender", size.getGender());
        data.put("maxHeight", size.getMaxHeight());
        data.put("minHeight", size.getMinHeight());
        data.put("maxWeight", size.getMaxWeight());
        data.put("minWeight", size.getMinWeight());
        data.put("enumName", size.name());

        return data;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> cancelOrder(CancelOrderRequest request) {
        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
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

    @Override
    public ResponseEntity<ResponseObject> viewSchoolOrderDetail(HttpServletRequest request, int orderId) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);
        }
        Order order = orderRepo.findByIdAndSchoolDesign_Customer_Account_Id(orderId, account.getId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Order not found", null);
        }

        Map<String, Object> data = EntityResponseBuilder.buildOrder(order, partnerRepo, deliveryItemRepo, designItemRepo, designQuotationRepo, designRequestRepo, transactionRepo);
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> confirmOrder(ConfirmOrderRequest request) {
        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }
        order.setStatus(Status.ORDER_COMPLETED);
        order.setCompletedDate(LocalDate.now());
        order.setDeliveryImage(request.getDeliveryImage());
        orderRepo.save(order);
        return ResponseBuilder.build(HttpStatus.OK, "Order completed successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> deleteSewingPhase(int sewingPhaseId, HttpServletRequest httpServletRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);
        }

        List<SewingPhase> sewingPhaseList = sewingPhaseRepo.findAllByGarment_Customer_Account_Id(account.getId());

        if (sewingPhaseList == null || sewingPhaseList.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "This garment factory doesn't have any sewing phase", null);
        }

        SewingPhase sewingPhase = sewingPhaseRepo.findById(sewingPhaseId).orElse(null);
        if (sewingPhase == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Sewing phase not found", null);
        }

        boolean belong = sewingPhaseList.stream()
                .anyMatch(sp -> sp.getId().equals(sewingPhase.getId()));

        if (!belong) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You don't have permission to delete this sewing phase", null);
        }

        List<Milestone> milestones = sewingPhase.getMilestones();

        boolean allCompleted = milestones.stream()
                .allMatch(milestone -> milestone.getStatus().equals(Status.MILESTONE_COMPLETED));

        if (!allCompleted) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST,
                    "Cannot delete sewing phase because some milestones are not completed", null);
        }

        sewingPhase.setStatus(Status.SEWING_PHASE_INACTIVE);
        sewingPhaseRepo.save(sewingPhase);
        return ResponseBuilder.build(HttpStatus.OK, "Sewing phase deleted successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> confirmDeliveredOrder(ConfirmDeliveredOrderRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);
        }

        Order order = orderRepo.findBySchoolDesign_Customer_Account_IdAndId(account.getId(), request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Order not found", null);
        }

        order.setStatus(Status.ORDER_DELIVERING);
        order.setShippingCode(request.getShippingCode());
        order.setShippingFee(request.getShippingFee());
        orderRepo.save(order);

        Partner garment = partnerRepo.findById(request.getCreateTransactionRequest().getReceiverId()).orElse(null);
        if (garment == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Garment not found", null);
        request.getCreateTransactionRequest().setReceiverId(garment.getCustomer().getId());
        return paymentService.createTransaction(request.getCreateTransactionRequest(), httpRequest);
    }

}
