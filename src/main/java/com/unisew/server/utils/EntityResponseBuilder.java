package com.unisew.server.utils;

import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.DeliveryItemRepo;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.SewingPhaseRepo;

import java.util.*;

public class EntityResponseBuilder {

    //-------Account---------
    public static Map<String, Object> buildAccountResponse(Account account) {
        List<String> keys = List.of(
                "id", "email", "role",
                "registerDate", "status"
        );
        List<Object> values = List.of(
                account.getId(), account.getEmail(), account.getRole().getValue(),
                account.getRegisterDate(), account.getStatus().getValue()

        );
        return MapUtils.build(keys, values);
    }

    //-------Account Request---------

    //-------Customer---------
    public static Map<String, Object> buildCustomerResponse(Customer customer) {
        List<String> keys = List.of(
                "id", "account",
                "address", "taxCode", "name",
                "business", "phone", "avatar"
        );
        List<Object> values = List.of(
                customer.getId(), buildAccountResponse(customer.getAccount()),
                customer.getAddress(), customer.getTaxCode(), customer.getName(),
                customer.getBusinessName(), customer.getPhone(), customer.getAvatar()

        );

        return MapUtils.build(keys, values);
    }

    //-------Delivery Item---------
    public static List<Map<String, Object>> buildDeliveryItemListResponse(List<DeliveryItem> items, DesignItemRepo designItemRepo) {
        return items.stream().map(item -> buildDeliveryItemResponse(item, designItemRepo)).toList();
    }

    public static Map<String, Object> buildDeliveryItemResponse(DeliveryItem item, DesignItemRepo designItemRepo) {
        if (item == null) return null;
        DesignItem designItem = designItemRepo.findById(item.getDesignItemId()).orElse(null);
        if (designItem == null) return null;
        List<String> keys = List.of(
                "id",
                "designItem",
                "baseLogoHeight",
                "baseLogoWidth",
                "frontImageUrl",
                "backImageUrl"
        );
        List<Object> values = List.of(
                item.getId(),
                buildDesignItemResponse(designItem),
                item.getBaseLogoHeight(),
                item.getBaseLogoWidth(),
                item.getFrontImageUrl(),
                item.getBackImageUrl()
        );
        return MapUtils.build(keys, values);
    }

    //-------Design Delivery---------

    public static List<Map<String, Object>> buildDesignDeliveryListResponse(List<DesignDelivery> deliveries, DesignItemRepo designItemRepo) {
        return deliveries.stream().map(delivery -> buildDesignDeliveryResponse(delivery, designItemRepo)).toList();
    }

    public static Map<String, Object> buildDesignDeliveryResponse(DesignDelivery delivery, DesignItemRepo designItemRepo) {
        if (delivery == null) return null;
        List<String> keys = List.of(
                "id",
                "name",
                "note",
                "revision",
                "submitDate",
                "version",
                "designRequest",
                "designItems"
        );


        List<Object> values = List.of(
                delivery.getId(),
                Objects.requireNonNullElse(delivery.getName(), ""),
                Objects.requireNonNullElse(delivery.getNote(), ""),
                delivery.isRevision(),
                delivery.getSubmitDate(),
                delivery.getVersion(),
                Objects.requireNonNullElse(buildDesignRequestResponse(delivery.getDesignRequest()), ""),
                Objects.requireNonNullElse(buildDeliveryItemListResponse(delivery.getDeliveryItems(), designItemRepo), "")
        );
        return MapUtils.build(keys, values);
    }

    //-------Design Item---------
    public static List<Map<String, Object>> buildDesignItemListResponse(List<DesignItem> items) {
        return items.stream().map(EntityResponseBuilder::buildDesignItemResponse).toList();
    }

    public static Map<String, Object> buildDesignItemResponse(DesignItem item) {
        if (item == null) return null;
        List<String> keys = List.of(
                "id", "type", "category", "logoPosition",
                "color", "note", "sampleImages", "fabricId",
                "fabricName", "gender", "logoImageUrl"
        );
        List<Object> values = List.of(
                item.getId(), item.getType().getValue(), item.getCategory().getValue(), item.getLogoPosition(),
                item.getColor(), item.getNote(), buildSampleImageListResponse(item.getSampleImages()), item.getFabric().getId(),
                item.getFabric().getName(), item.getGender().getValue(), item.getDesignRequest().getLogoImage()
        );
        return MapUtils.build(keys, values);
    }

    //-------Design Quotation---------
    public static List<Map<String, Object>> buildDesignQuotationListResponse(List<DesignQuotation> quotations) {
        return quotations.stream()
                .filter(quotation -> quotation.getStatus().equals(Status.DESIGN_QUOTATION_PENDING))
                .map(EntityResponseBuilder::buildDesignQuotationResponse)
                .toList();
    }

    public static Map<String, Object> buildDesignQuotationResponse(DesignQuotation quotation) {
        List<String> keys = List.of(
                "id", "designer", "note",
                "deliveryWithIn", "revisionTime",
                "extraRevisionPrice", "price",
                "acceptanceDeadline", "status"
        );
        List<Object> values = List.of(
                quotation.getId(), buildPartnerResponse(quotation.getDesigner()), quotation.getNote(),
                quotation.getDeliveryWithIn(), quotation.getRevisionTime(),
                quotation.getExtraRevisionPrice(), quotation.getPrice(),
                quotation.getAcceptanceDeadline(), quotation.getStatus().getValue()

        );
        return MapUtils.build(keys, values);
    }

    //-------Design Request---------
    public static Map<String, Object> buildDesignRequestResponse(DesignRequest request) {
        List<String> keys = List.of(
                "id", "school",
                "name", "creationDate", "logoImage",
                "privacy", "status", "items"
        );
        List<Object> values = List.of(
                request.getId(), buildCustomerResponse(request.getSchool()),
                request.getName(), request.getCreationDate(), request.getLogoImage(),
                request.isPrivacy(), request.getStatus().getValue(), buildDesignItemListResponse(request.getDesignItems())
        );

        return MapUtils.build(keys, values);
    }

    //-------Fabric---------

    //-------Feedback---------
    public static List<Map<String, Object>> buildListFeedbackResponse(List<Feedback> feedbacks) {
        return feedbacks.stream().map(EntityResponseBuilder::buildFeedbackResponse).toList();
    }

    public static Map<String, Object> buildFeedbackResponse(Feedback feedback) {
        if (feedback == null) return null;
        List<String> keys = List.of("id", "rating", "content", "creationDate", "images");
        List<Object> values = List.of(feedback.getId(), feedback.getRating(), feedback.getContent(), feedback.getCreationDate(), buildFeedbackImageListResponse(feedback.getFeedbackImages()));
        return MapUtils.build(keys, values);
    }

    //-------Feedback Image---------
    public static List<Map<String, Object>> buildFeedbackImageListResponse(List<FeedbackImage> images) {
        return images.stream().map(EntityResponseBuilder::buildFeedbackImageResponse).toList();
    }

    public static Map<String, Object> buildFeedbackImageResponse(FeedbackImage image) {
        if (image == null) return null;
        List<String> keys = List.of("id", "url");
        List<Object> values = List.of(image.getId(), image.getImageUrl());
        return MapUtils.build(keys, values);
    }

    //-------Order---------
    public static List<Map<String, Object>> buildOrderList(List<Order> orders, PartnerRepo partnerRepo, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo, SewingPhaseRepo sewingPhaseRepo) {
        return orders.stream()
                .map(order -> {
                    Partner partner;
                    if (order.getGarmentId() == null) partner = null;
                    else partner = partnerRepo.findById(order.getId()).orElse(null);

                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("deadline", order.getDeadline());
                    orderMap.put("school", buildCustomerResponse(order.getSchoolDesign().getCustomer()));
                    orderMap.put("garment", EntityResponseBuilder.buildPartnerResponse(partner));
                    orderMap.put("note", order.getNote());
                    orderMap.put("orderDate", order.getOrderDate());
                    orderMap.put("price", order.getPrice());
                    orderMap.put("serviceFee", order.getServiceFee());
                    orderMap.put("status", order.getStatus().getValue());
                    orderMap.put("orderDetails", EntityResponseBuilder.buildOrderDetailList(order.getOrderDetails(), deliveryItemRepo, designItemRepo));
                    orderMap.put("milestone", EntityResponseBuilder.buildOrderMilestoneList(order.getMilestones()));
                    return orderMap;
                })
                .toList();
    }

    //-------Order Detail---------
    public static List<Map<String, Object>> buildOrderDetailList(List<OrderDetail> orderDetails, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo) {
        return orderDetails.stream()
                .map(detail -> buildOrderDetail(detail, deliveryItemRepo, designItemRepo))
                .toList();
    }

    public static Map<String, Object> buildOrderDetail(OrderDetail detail, DeliveryItemRepo deliveryItemRepo, DesignItemRepo designItemRepo) {
        if (detail == null) return null;
        List<String> keys = List.of(
                "id", "deliveryItem",
                "quantity", "size"
        );

        DeliveryItem item = deliveryItemRepo.findById(detail.getDeliveryItemId()).orElse(null);

        List<Object> values = List.of(
                detail.getId(), item == null ? "" : buildDeliveryItemResponse(item, designItemRepo),
                detail.getQuantity(), detail.getSize().getSize()
        );
        return MapUtils.build(keys, values);
    }

    //-------Order Milestone---------
    public static List<Map<String, Object>> buildOrderMilestoneList(List<Milestone> milestones) {
        return milestones.stream()
                .map(EntityResponseBuilder::buildOrderMilestone)
                .toList();
    }

    public static Map<String, Object> buildOrderMilestone(Milestone milestone) {
        if (milestone == null) return null;
        List<String> keys = List.of(
                "id", "name", "description", "stage", "imageUrl",
                "startDate", "endDate", "status"
        );
        List<Object> values = List.of(
                milestone.getId(), milestone.getPhase().getName(), milestone.getPhase().getDescription(), milestone.getStage(), Objects.requireNonNullElse(milestone.getImgUrl(), ""),
                milestone.getStartDate(), milestone.getEndDate(), milestone.getStatus().getValue()
        );
        return MapUtils.build(keys, values);
    }

    //-------Sewing Phase---------
    public static List<Map<String, Object>> buildSewingPhaseList(List<SewingPhase> phases) {
        return phases.stream()
                .map(EntityResponseBuilder::buildSewingPhaseResponse)
                .toList();
    }

    private static Map<String, Object> buildSewingPhaseResponse(SewingPhase sewingPhase) {
        if (sewingPhase == null) return null;
        List<String> keys = List.of(
                "id", "name", "description", "status"
        );
        List<Object> values = List.of(
                sewingPhase.getId(), sewingPhase.getName(), sewingPhase.getDescription(), sewingPhase.getStatus().getValue()
        );
        return MapUtils.build(keys, values);
    }

    //-------Partner---------
    public static Map<String, Object> buildPartnerResponse(Partner partner) {
        if(partner == null) return null;
        List<String> keys = List.of(
                "id", "customer",
                "preview",
                "startTime", "endTime",
                "rating", "busy", "thumbnails"
        );
        List<Object> values = List.of(
                partner.getId(), buildCustomerResponse(partner.getCustomer()),
                partner.getInsidePreview(),
                partner.getStartTime(), partner.getEndTime(),
                partner.getRating(), partner.isBusy(), buildThumbnailImageListResponse(partner.getThumbnailImages())
        );

        return MapUtils.build(keys, values);
    }

    //-------Quotation---------

    public static List<Map<String, Object>> buildQuotationResponse(List<GarmentQuotation> garmentQuotations) {
        return (garmentQuotations != null) ?
                garmentQuotations.stream().map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    map.put("garmentId", Objects.requireNonNullElse(item.getGarment().getId(), null));
                    map.put("garmentName", item.getGarment().getCustomer().getName());
                    map.put("earlyDeliveryDate", item.getEarlyDeliveryDate());
                    map.put("acceptanceDeadline", item.getAcceptanceDeadline());
                    map.put("price", item.getPrice());
                    map.put("note", item.getNote());
                    map.put("status", item.getStatus());
                    return map;
                }).toList()
                :
                new ArrayList<>();
    }

    //-------Revision Request---------
    public static Map<String, Object> buildRevisionRequestResponse(RevisionRequest request) {
        if (request == null) return null;
        List<String> keys = List.of(
                "id",
                "requestDate",
                "note"
        );
        List<Object> values = List.of(
                request.getId(),
                request.getRequestDate(),
                request.getNote()
        );
        return MapUtils.build(keys, values);
    }

    //-------Sample Image---------
    public static List<Map<String, Object>> buildSampleImageListResponse(List<SampleImage> images) {
        return images.stream().map(EntityResponseBuilder::buildSampleImageResponse).toList();
    }

    public static Map<String, Object> buildSampleImageResponse(SampleImage image) {
        if (image == null) return null;
        List<String> keys = List.of("id", "url");
        List<Object> values = List.of(image.getId(), image.getImageUrl());
        return MapUtils.build(keys, values);
    }

    //-------School Design---------
    public static List<Map<String, Object>> buildSchoolDesignListResponse(List<SchoolDesign> designs, DesignItemRepo designItemRepo) {
        return designs.stream().map(design -> buildSchoolDesignResponse(design, designItemRepo)).toList();
    }

    public static Map<String, Object> buildSchoolDesignResponse(SchoolDesign design, DesignItemRepo designItemRepo) {
        if (design == null) return null;
        List<String> keys = List.of("id", "school", "delivery");
        List<Object> values = List.of(
                design.getId(), buildCustomerResponse(design.getCustomer()),
                buildDesignDeliveryResponse(design.getDesignDelivery(), designItemRepo)
        );
        return MapUtils.build(keys, values);
    }

    //-------Thumbnail Image---------
    public static List<Map<String, Object>> buildThumbnailImageListResponse(List<ThumbnailImage> images) {
        return images.stream().map(EntityResponseBuilder::buildThumbnailImageResponse).toList();
    }

    public static Map<String, Object> buildThumbnailImageResponse(ThumbnailImage image) {
        if (image == null) return null;
        List<String> keys = List.of("id", "url");
        List<Object> values = List.of(image.getId(), image.getImageUrl());
        return MapUtils.build(keys, values);
    }

    //-------Transaction---------

    //-------Wallet---------


}
