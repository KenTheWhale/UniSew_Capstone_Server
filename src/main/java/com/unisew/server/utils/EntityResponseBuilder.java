package com.unisew.server.utils;

import com.unisew.server.enums.PaymentType;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Customer;
import com.unisew.server.models.DeliveryItem;
import com.unisew.server.models.DesignDelivery;
import com.unisew.server.models.DesignItem;
import com.unisew.server.models.DesignQuotation;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.Fabric;
import com.unisew.server.models.Feedback;
import com.unisew.server.models.FeedbackImage;
import com.unisew.server.models.GarmentQuotation;
import com.unisew.server.models.Milestone;
import com.unisew.server.models.Order;
import com.unisew.server.models.OrderDetail;
import com.unisew.server.models.Partner;
import com.unisew.server.models.RevisionRequest;
import com.unisew.server.models.SampleImage;
import com.unisew.server.models.SchoolDesign;
import com.unisew.server.models.SewingPhase;
import com.unisew.server.models.ThumbnailImage;
import com.unisew.server.models.Transaction;
import com.unisew.server.repositories.DeliveryItemRepo;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.DesignQuotationRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.TransactionRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EntityResponseBuilder {

    //-------Account---------
    public static Map<String, Object> buildAccountResponse(Account account) {
        if(account == null) return null;

        Map<String, Object> data = new HashMap<>();
        data.put("id", account.getId());
        data.put("email", account.getEmail());
        data.put("role", account.getRole().getValue());
        data.put("registerDate", account.getRegisterDate());
        data.put("status", account.getStatus().getValue());
        return data;
    }

    //-------Account Request---------

    //-------Customer---------
    public static Map<String, Object> buildCustomerResponse(Customer customer) {
        if(customer == null) return null;

        Map<String, Object> data = new HashMap<>();

        data.put("id", customer.getId());
        data.put("account", buildAccountResponse(customer.getAccount()));
        data.put("address", customer.getAddress());
        data.put("taxCode", customer.getTaxCode());
        data.put("name", customer.getName());
        data.put("business", customer.getBusinessName());
        data.put("phone", customer.getPhone());
        data.put("avatar", customer.getAvatar());

        return data;
    }

    //-------Delivery Item---------
    public static List<Map<String, Object>> buildDeliveryItemListResponse(List<DeliveryItem> items, DesignItemRepo designItemRepo) {
        return items.stream().map(item -> buildDeliveryItemResponse(item, designItemRepo)).toList();
    }

    public static Map<String, Object> buildDeliveryItemResponse(DeliveryItem item, DesignItemRepo designItemRepo) {
        if (item == null) {
            return null;
        }

        DesignItem designItem = designItemRepo.findById(item.getDesignItemId()).orElse(null);
        if (designItem == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", item.getId());
        data.put("designItem", buildDesignItemResponse(designItem));
        data.put("baseLogoHeight", item.getBaseLogoHeight());
        data.put("baseLogoWidth", item.getBaseLogoWidth());
        data.put("frontImageUrl", item.getFrontImageUrl());
        data.put("backImageUrl", item.getBackImageUrl());

        return data;
    }

    //-------Design Delivery---------

    public static List<Map<String, Object>> buildDesignDeliveryListResponse(List<DesignDelivery> deliveries, DesignItemRepo designItemRepo) {
        return deliveries.stream().map(delivery -> buildDesignDeliveryResponse(delivery, designItemRepo)).toList();
    }

    public static Map<String, Object> buildDesignDeliveryResponse(DesignDelivery delivery, DesignItemRepo designItemRepo) {
        if (delivery == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", delivery.getId());
        data.put("name", delivery.getName());
        data.put("note", delivery.getNote());
        data.put("revision", delivery.isRevision());
        data.put("submitDate", delivery.getSubmitDate());
        data.put("version", delivery.getVersion());
        data.put("designRequest", buildDesignRequestResponse(delivery.getDesignRequest()));
        data.put("deliveryItems", buildDeliveryItemListResponse(delivery.getDeliveryItems(), designItemRepo));

        return data;
    }

    //-------Design Item---------
    public static List<Map<String, Object>> buildDesignItemListResponse(List<DesignItem> items) {
        return items.stream().map(EntityResponseBuilder::buildDesignItemResponse).toList();
    }

    public static Map<String, Object> buildDesignItemResponse(DesignItem item) {
        if (item == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", item.getId());
        data.put("type", item.getType().getValue());
        data.put("category", item.getCategory().getValue());
        data.put("logoPosition", item.getLogoPosition());
        data.put("color", item.getColor());
        data.put("note", item.getNote());
        data.put("sampleImages", buildSampleImageListResponse(item.getSampleImages()));
        data.put("fabricId", item.getFabric().getId());
        data.put("fabricName", item.getFabric().getName());
        data.put("gender", item.getGender().getValue());
        data.put("logoImageUrl", item.getDesignRequest().getLogoImage());

        return data;
    }

    //-------Design Quotation---------
    public static List<Map<String, Object>> buildDesignQuotationListResponse(List<DesignQuotation> quotations, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo) {

        return quotations.stream()
                .peek(quotation -> {
                    if (LocalDate.now().isAfter(quotation.getAcceptanceDeadline())) {
                        quotation.setStatus(Status.DESIGN_QUOTATION_REJECTED);
                        designQuotationRepo.save(quotation);
                    }
                })
                .filter(quotation -> quotation.getStatus().equals(Status.DESIGN_QUOTATION_PENDING))
                .map(quotation -> buildDesignQuotationResponse(quotation, designQuotationRepo, designRequestRepo))
                .toList();
    }

    public static Map<String, Object> buildDesignQuotationResponse(DesignQuotation quotation, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo) {
        if (quotation == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", quotation.getId());
        data.put("designer", buildPartnerResponse(quotation.getDesigner(), designQuotationRepo, designRequestRepo));
        data.put("note", quotation.getNote());
        data.put("deliveryWithIn", quotation.getDeliveryWithIn());
        data.put("revisionTime", quotation.getRevisionTime());
        data.put("extraRevisionPrice", quotation.getExtraRevisionPrice());
        data.put("price", quotation.getPrice());
        data.put("acceptanceDeadline", quotation.getAcceptanceDeadline());
        data.put("status", quotation.getStatus().getValue());

        return data;
    }

    //-------Design Request---------
    public static List<Map<String, Object>> buildDesignRequestListForAdminResponse(List<DesignRequest> requests, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo) {
        return requests.stream().map(request -> buildDesignRequestForAdminResponse(request, designQuotationRepo, designRequestRepo)).toList();
    }

    public static Map<String, Object> buildDesignRequestResponse(DesignRequest request) {
        if (request == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", request.getId());
        data.put("school", buildCustomerResponse(request.getSchool()));
        data.put("name", request.getName());
        data.put("creationDate", request.getCreationDate());
        data.put("logoImage", request.getLogoImage());
        data.put("privacy", request.isPrivacy());
        data.put("status", request.getStatus().getValue());
        data.put("items", buildDesignItemListResponse(request.getDesignItems()));
        data.put("cancelReason", request.getCancelReason());
        data.put("feedback", Objects.requireNonNullElse(buildFeedbackResponse(request.getFeedback()), ""));

        return data;
    }

    public static Map<String, Object> buildDesignRequestForAdminResponse(DesignRequest request, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo) {
        if (request == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", request.getId());
        data.put("school", buildCustomerResponse(request.getSchool()));
        data.put("name", request.getName());
        data.put("creationDate", request.getCreationDate());
        data.put("logoImage", request.getLogoImage());
        data.put("privacy", request.isPrivacy());
        data.put("status", request.getStatus().getValue());
        data.put("items", buildDesignItemListResponse(request.getDesignItems()));
        data.put("feedback", Objects.requireNonNullElse(buildFeedbackResponse(request.getFeedback()), ""));
        var quotationId = request.getDesignQuotationId();
        var dq = (quotationId == null) ? null
                : designQuotationRepo.findById(quotationId).orElse(null);
        data.put("quotation", (dq != null) ? buildDesignQuotationResponse(dq, designQuotationRepo, designRequestRepo) : "");

        return data;
    }

    //-------Fabric---------
    public static List<Map<String, Object>> buildListFabricResponse(List<Fabric> fabrics) {
        return fabrics.stream().map(EntityResponseBuilder::buildFabricResponse).toList();
    }

    public static Map<String, Object> buildFabricResponse(Fabric fabric) {
        if (fabric == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", fabric.getId());
        data.put("name", fabric.getName());
        data.put("description", fabric.getDescription());
        data.put("clothType", fabric.getDesignItemType().getValue());
        data.put("clothCategory", fabric.getDesignItemCategory().getValue());

        return data;
    }

    //-------Feedback---------
    public static List<Map<String, Object>> buildListFeedbackResponse(List<Feedback> feedbacks) {
        return feedbacks.stream().map(EntityResponseBuilder::buildFeedbackResponse).toList();
    }

    public static Map<String, Object> buildFeedbackResponse(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", feedback.getId());
        data.put("rating", feedback.getRating());
        data.put("schoolContent", feedback.getSchoolContent());
        data.put("partnerContent", feedback.getPartnerContent());
        data.put("creationDate", feedback.getCreationDate());
        data.put("images", buildFeedbackImageListResponse(feedback.getFeedbackImages()));
        data.put("status", feedback.getStatus().getValue());
        data.put("schoolVideo", feedback.getSchoolVideoUrl());
        data.put("partnerVideo", feedback.getPartnerVideoUrl());
        data.put("report", feedback.isReport());
        data.put("sender", buildSenderMap(feedback));
        data.put("receiver", buildReceiverMap(feedback));

        return data;
    }

    public static List<Map<String, Object>> buildListReportResponse(List<Feedback> feedbacks, PartnerRepo partnerRepo, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo, TransactionRepo transactionRepo) {
        return feedbacks.stream().map(
                feedback -> buildReportResponse(feedback, partnerRepo, deliveryItemRepo, designItemRepo, designQuotationRepo, designRequestRepo, transactionRepo)
        ).toList();
    }

    public static Map<String, Object> buildReportResponse(Feedback feedback, PartnerRepo partnerRepo, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo, TransactionRepo transactionRepo) {
        if (feedback == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", feedback.getId());
        data.put("rating", feedback.getRating());
        data.put("schoolContent", feedback.getSchoolContent());
        data.put("partnerContent", feedback.getPartnerContent());
        data.put("creationDate", feedback.getCreationDate());
        data.put("images", buildFeedbackImageListResponse(feedback.getFeedbackImages()));
        data.put("schoolVideo", feedback.getSchoolVideoUrl());
        data.put("partnerVideo", feedback.getPartnerVideoUrl());
        data.put("status", feedback.getStatus().getValue());
        data.put("report", feedback.isReport());
        data.put("sender", Objects.requireNonNullElse(buildSenderMap(feedback), ""));
        data.put("receiver", Objects.requireNonNullElse(buildReceiverMap(feedback), ""));
        data.put("order", buildOrder(feedback.getOrder(), partnerRepo, deliveryItemRepo, designItemRepo, designQuotationRepo, designRequestRepo, transactionRepo));
        data.put("designRequest", buildDesignRequestResponse(feedback.getDesignRequest()));

        return data;
    }

    private static Map<String, Object> buildSenderMap(Feedback fb) {
        Customer school = null;
        if (fb.getDesignRequest() != null) {
            school = fb.getDesignRequest().getSchool();
        } else if (fb.getOrder() != null
                && fb.getOrder().getSchoolDesign() != null) {
            school = fb.getOrder().getSchoolDesign().getCustomer();
        }
        if (school == null) return null;

        Map<String, Object> sender = new HashMap<>();
        sender.put("type", "school");
        sender.put("id", school.getId());
        sender.put("name", school.getName());
        sender.put("business", school.getBusinessName());
        sender.put("avatar", school.getAvatar());
        if (school.getAccount() != null) {
            sender.put("email", school.getAccount().getEmail());
        }
        return sender;
    }

    private static Map<String, Object> buildReceiverMap(Feedback fb) {
        if (fb.getDesignRequest() != null) {
            Partner designer = null;
            List<DesignQuotation> quotations = fb.getDesignRequest().getDesignQuotations();
            if (quotations != null && !quotations.isEmpty()) {
                designer = quotations.stream()
                        .filter(q -> q.getStatus() == Status.DESIGN_QUOTATION_SELECTED)
                        .map(DesignQuotation::getDesigner)
                        .findFirst()
                        .orElseGet(() ->
                                quotations.stream()
                                        .map(DesignQuotation::getDesigner)
                                        .findFirst()
                                        .orElse(null)
                        );
            }
            if (designer == null) return null;

            Map<String, Object> receiver = new HashMap<>();
            receiver.put("type", "designer");
            receiver.put("id", designer.getId());
            if (designer.getCustomer() != null) {
                receiver.put("name", designer.getCustomer().getName());
                receiver.put("avatar", designer.getCustomer().getAvatar());
                if (designer.getCustomer().getAccount() != null) {
                    receiver.put("email", designer.getCustomer().getAccount().getEmail());
                }
            }
            return receiver;
        }

        if (fb.getOrder() != null) {
            Order order = fb.getOrder();
            GarmentQuotation gq = order.getGarmentQuotations().stream()
                    .filter(o -> o.getGarment().getId().equals(order.getGarmentId()))
                    .findFirst()
                    .orElse(null);
            Partner garment = gq != null ? gq.getGarment() : null;
            assert garment != null;
            Map<String, Object> receiver = new HashMap<>();
            receiver.put("type", "garment");
            receiver.put("id", order.getGarmentId());
            receiver.put("name", order.getGarmentName());
            receiver.put("avatar", garment.getCustomer().getAvatar());
            receiver.put("email", garment.getCustomer().getAccount().getEmail());
            return receiver;
        }

        return null;
    }

    //-------Feedback Image---------
    public static List<Map<String, Object>> buildFeedbackImageListResponse(List<FeedbackImage> images) {
        return images.stream().map(EntityResponseBuilder::buildFeedbackImageResponse).toList();
    }

    public static Map<String, Object> buildFeedbackImageResponse(FeedbackImage image) {
        if (image == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", image.getId());
        data.put("owner", image.getOwner());
        data.put("url", image.getImageUrl());

        return data;
    }

    //-------Order---------
    public static List<Map<String, Object>> buildOrderList(List<Order> orders, PartnerRepo partnerRepo, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo, DesignRequestRepo designRequestRepo, DesignQuotationRepo designQuotationRepo, TransactionRepo transactionRepo) {
        return orders.stream()
                .map(order -> buildOrder(order, partnerRepo, deliveryItemRepo, designItemRepo, designQuotationRepo, designRequestRepo, transactionRepo))
                .toList();
    }

    public static Map<String, Object> buildOrder(Order order, PartnerRepo partnerRepo, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo, TransactionRepo transactionRepo) {
        Partner partner;
        if (order == null) return null;
        if (order.getGarmentId() == null) partner = null;
        else partner = partnerRepo.findById(order.getGarmentId()).orElse(null);
        GarmentQuotation quotation = order.getGarmentQuotations().stream().filter(q -> Objects.equals(q.getGarment().getId(), order.getGarmentId())).findFirst().orElse(null);
        Transaction transaction = transactionRepo.findAllByItemIdAndPaymentType(order.getId(), PaymentType.DEPOSIT).stream().findFirst().orElse(null);

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", order.getId());
        orderMap.put("deadline", order.getDeadline());
        orderMap.put("garmentQuotationId", order.getGarmentQuotationId());
        orderMap.put("school", buildCustomerResponse(order.getSchoolDesign().getCustomer()));
        orderMap.put("garment", EntityResponseBuilder.buildPartnerResponse(partner, designQuotationRepo, designRequestRepo));
        orderMap.put("note", order.getNote());
        orderMap.put("orderDate", order.getOrderDate());
        orderMap.put("price", order.getPrice());
        orderMap.put("shippingFee", order.getShippingFee());
        orderMap.put("serviceFee", transaction != null ? transaction.getServiceFee() : null);
        orderMap.put("status", order.getStatus().getValue());
        orderMap.put("orderDetails", EntityResponseBuilder.buildOrderDetailList(order.getOrderDetails(), deliveryItemRepo, designItemRepo));
        orderMap.put("milestone", EntityResponseBuilder.buildOrderMilestoneList(order.getMilestones()));
        orderMap.put("selectedDesign", EntityResponseBuilder.buildDesignDeliveryResponse(order.getSchoolDesign().getDesignDelivery(), designItemRepo));
        orderMap.put("feedback", Objects.requireNonNullElse(buildFeedbackResponse(order.getFeedback()), ""));
        orderMap.put("shippingCode", Objects.requireNonNullElse(order.getShippingCode(), ""));
        orderMap.put("depositRate", quotation == null ? 0 : quotation.getDepositRate() / 100);
        orderMap.put("completedDate", order.getCompletedDate());
        orderMap.put("deliveryImage", order.getDeliveryImage());
        orderMap.put("cancelReason", order.getCancelReason());
        orderMap.put("deliveryAddress", order.getDeliveryAddress() == null ? "" : order.getDeliveryAddress());
        orderMap.put("preDeliveryImageUrl", order.getPreDeliveryImage());
        orderMap.put("transactions", buildListTransactionResponse(transactionRepo.findAllByItemId(order.getId())));

        return orderMap;
    }

    //-------Order Detail---------
    public static List<Map<String, Object>> buildOrderDetailList(List<OrderDetail> orderDetails, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo) {
        return orderDetails.stream()
                .map(detail -> buildOrderDetail(detail, deliveryItemRepo, designItemRepo))
                .toList();
    }

    public static Map<String, Object> buildOrderDetail(OrderDetail detail, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo) {
        if (detail == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        DeliveryItem item = deliveryItemRepo.findById(detail.getDeliveryItemId()).orElse(null);

        data.put("id", detail.getId());
        data.put("deliveryItem", buildDeliveryItemResponse(item, designItemRepo));
        data.put("quantity", detail.getQuantity());
        data.put("size", detail.getSize().getSize());

        return data;
    }

    //-------Order Milestone---------
    public static List<Map<String, Object>> buildOrderMilestoneList(List<Milestone> milestones) {
        return milestones.stream()
                .map(EntityResponseBuilder::buildOrderMilestone)
                .toList();
    }

    public static Map<String, Object> buildOrderMilestone(Milestone milestone) {
        if (milestone == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", milestone.getId());
        data.put("name", milestone.getPhase().getName());
        data.put("description", milestone.getPhase().getDescription());
        data.put("stage", milestone.getStage());
        data.put("videoUrl", Objects.requireNonNullElse(milestone.getVideoUrl(), ""));
        data.put("startDate", milestone.getStartDate());
        data.put("endDate", milestone.getEndDate());
        data.put("status", milestone.getStatus().getValue());
        data.put("completedDate", Objects.requireNonNullElse(milestone.getCompletedDate(), ""));

        return data;
    }

    //-------Sewing Phase---------
    public static List<Map<String, Object>> buildSewingPhaseList(List<SewingPhase> phases) {
        return phases.stream()
                .map(EntityResponseBuilder::buildSewingPhaseResponse)
                .toList();
    }

    private static Map<String, Object> buildSewingPhaseResponse(SewingPhase sewingPhase) {
        if (sewingPhase == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", sewingPhase.getId());
        data.put("name", sewingPhase.getName());
        data.put("description", sewingPhase.getDescription());
        data.put("status", sewingPhase.getStatus().getValue());

        return data;
    }

    //-------Partner---------
    public static Map<String, Object> buildPartnerResponse(Partner partner, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo) {
        if (partner == null) {
            return null;
        }

        List<Feedback> feedbacks = getDesignerFeedbacks(partner.getId(), designQuotationRepo, designRequestRepo);

        Map<String, Object> data = new HashMap<>();

        data.put("id", partner.getId());
        data.put("customer", buildCustomerResponse(partner.getCustomer()));
        data.put("preview", partner.getInsidePreview());
        data.put("startTime", partner.getStartTime());
        data.put("endTime", partner.getEndTime());
        data.put("rating", partner.getRating());
        data.put("thumbnails", buildThumbnailImageListResponse(partner.getThumbnailImages()));
        data.put("feedbacks", buildListFeedbackResponse(feedbacks));
        data.put("shippingUID", partner.getCustomer().getAccount().getRole().equals(Role.GARMENT) ? partner.getShippingUid() : "");

        return data;
    }

    private static List<Feedback> getDesignerFeedbacks(Integer designerId, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo) {
        Set<Integer> designerQuotationIds = designQuotationRepo.findAllByDesigner_Id(designerId)
                .stream()
                .map(DesignQuotation::getId)
                .collect(java.util.stream.Collectors.toSet());

        List<DesignRequest> requestsWithFeedback = designRequestRepo.findAllByFeedbackIsNotNull()
                .stream()
                .filter(dr -> dr.getDesignQuotationId() != null && designerQuotationIds.contains(dr.getDesignQuotationId()))
                .toList();

        return requestsWithFeedback.stream()
                .map(DesignRequest::getFeedback)
                .filter(Objects::nonNull)
                .toList();
    }

    //-------Quotation---------

    public static List<Map<String, Object>> buildQuotationResponse(List<GarmentQuotation> garmentQuotations, DesignRequestRepo designRequestRepo, DesignQuotationRepo designQuotationRepo) {
        return garmentQuotations.stream().map(q -> buildQuotationResponse(q, designRequestRepo, designQuotationRepo)).toList();
    }

    public static Map<String, Object> buildQuotationResponse(GarmentQuotation garmentQuotation, DesignRequestRepo designRequestRepo, DesignQuotationRepo designQuotationRepo){
        if(garmentQuotation == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", garmentQuotation.getId());
        map.put("garment", buildPartnerResponse(garmentQuotation.getGarment(), designQuotationRepo, designRequestRepo));
        map.put("earlyDeliveryDate", garmentQuotation.getEarlyDeliveryDate());
        map.put("acceptanceDeadline", garmentQuotation.getAcceptanceDeadline());
        map.put("price", garmentQuotation.getPrice());
        map.put("note", garmentQuotation.getNote());
        map.put("depositRate", garmentQuotation.getDepositRate());
        map.put("status", garmentQuotation.getStatus().getValue());
        return map;
    }

    //-------Revision Request---------
    public static Map<String, Object> buildRevisionRequestResponse(RevisionRequest request) {
        if (request == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", request.getId());
        data.put("requestDate", request.getRequestDate());
        data.put("note", request.getNote());

        return data;
    }

    //-------Sample Image---------
    public static List<Map<String, Object>> buildSampleImageListResponse(List<SampleImage> images) {
        return images.stream().map(EntityResponseBuilder::buildSampleImageResponse).toList();
    }

    public static Map<String, Object> buildSampleImageResponse(SampleImage image) {
        if (image == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", image.getId());
        data.put("url", image.getImageUrl());

        return data;
    }

    //-------School Design---------
    public static List<Map<String, Object>> buildSchoolDesignListResponse(List<SchoolDesign> designs, DesignItemRepo designItemRepo) {
        return designs.stream().map(design -> buildSchoolDesignResponse(design, designItemRepo)).toList();
    }

    public static Map<String, Object> buildSchoolDesignResponse(SchoolDesign design, DesignItemRepo designItemRepo) {
        if (design == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", design.getId());
        data.put("school", buildCustomerResponse(design.getCustomer()));
        data.put("delivery", buildDesignDeliveryResponse(design.getDesignDelivery(), designItemRepo));

        return data;
    }

    //-------Thumbnail Image---------
    public static List<Map<String, Object>> buildThumbnailImageListResponse(List<ThumbnailImage> images) {
        return images.stream().map(EntityResponseBuilder::buildThumbnailImageResponse).toList();
    }

    public static Map<String, Object> buildThumbnailImageResponse(ThumbnailImage image) {
        if (image == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", image.getId());
        data.put("url", image.getImageUrl());

        return data;
    }

    //-------Transaction---------
    public static List<Map<String, Object>> buildListTransactionResponse(List<Transaction> transactions) {
        return transactions.stream().map(EntityResponseBuilder::buildTransactionResponse).toList();
    }

    private static Map<String, Object> buildTransactionResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", transaction.getId());
        data.put("sender", buildCustomerResponse(transaction.getSender()));
        data.put("receiver", buildCustomerResponse(transaction.getReceiver()));
        data.put("cardOwner", Objects.requireNonNullElse(transaction.getWallet().getCardOwner(), ""));
        data.put("amount", transaction.getAmount());
        data.put("creationDate", transaction.getCreationDate());
        data.put("serviceFee", transaction.getServiceFee());
        data.put("balanceType", transaction.getBalanceType());
        data.put("status", transaction.getStatus().getValue());
        data.put("paymentType", transaction.getPaymentType().getValue());
        data.put("itemId", transaction.getItemId());
        data.put("paymentGatewayCode", transaction.getPaymentGatewayCode());
        data.put("remain",(Map<String, Object>) transaction.getRemainingBalance());

        return data;
    }


    //-------Wallet---------


}
