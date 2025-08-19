package com.unisew.server.services.implementors;

import com.unisew.server.enums.*;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.PaymentService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
import com.unisew.server.utils.MapUtils;
import com.unisew.server.utils.ResponseBuilder;
import com.unisew.server.validations.BuyRevisionValidation;
import com.unisew.server.validations.CreateDesignValidation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignServiceImpl implements DesignService {

    private final DesignRequestRepo designRequestRepo;
    private final FabricRepo fabricRepo;
    private final SampleImageRepo sampleImageRepo;
    private final DesignItemRepo designItemRepo;
    private final DesignQuotationRepo designQuotationRepo;
    private final JWTService jwtService;
    private final AccountRepo accountRepo;
    private final DesignDeliveryRepo designDeliveryRepo;
    private final RevisionRequestRepo revisionRequestRepo;
    private final DeliveryItemRepo deliveryItemRepo;
    private final DesignCommentRepo designCommentRepo;
    private final SchoolDesignRepo schoolDesignRepo;
    private final PaymentService paymentService;
    private final PartnerRepo partnerRepo;


    //-----------------------------------DESIGN_REQUEST---------------------------------------//
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest, HttpServletRequest httpRequest) {

        String errorMessage = CreateDesignValidation.validate(createDesignRequest);

        if (!errorMessage.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, errorMessage, null);
        }

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        DesignRequest designRequest = DesignRequest.builder()
                .school(account.getCustomer())
                .creationDate(LocalDate.now())
                .logoImage(createDesignRequest.getLogoImage())
                .name(createDesignRequest.getDesignName())
                .status(Status.DESIGN_REQUEST_PENDING)
                .privacy(true)
                .revisionTime(0)
                .build();

        designRequestRepo.save(designRequest);
        for (CreateDesignRequest.Item item : createDesignRequest.getDesignItem()) {

            Fabric fabric = fabricRepo.findById(item.getFabricId()).orElse(null);


            DesignItem newDesignItem = designItemRepo.save(
                    DesignItem.builder()
                            .fabric(fabric)
                            .designRequest(designRequest)
                            .category(DesignItemCategory.valueOf(item.getItemCategory().toUpperCase()))
                            .color(item.getColor())
                            .gender(Gender.valueOf(item.getGender().toUpperCase()))
                            .logoPosition(item.getLogoPosition())
                            .note(item.getNote())
                            .type(DesignItemType.valueOf(item.getItemType().toUpperCase()))
                            .build());

            if (item.getDesignType().equalsIgnoreCase("UPLOAD")) {
                createSampleImageByItem(newDesignItem, item.getUploadImage());
            }

        }


        return ResponseBuilder.build(HttpStatus.CREATED, "Create successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> viewListDesignRequest() {
        List<DesignRequest> designRequests = designRequestRepo.findAll()
                .stream()
                .filter(designRequest -> designRequest.getStatus().equals(Status.DESIGN_REQUEST_PENDING))
                .toList();

        return buildDesignRequestResponseForDesigner(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> getListDesignRequestBySchool(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<DesignRequest> designRequests = designRequestRepo.findAllBySchool_Id(account.getCustomer().getId());

        return buildDesignRequestResponseForSchool(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> getDesignRequestDetailForSchool(int id) {
        DesignRequest designRequest = designRequestRepo.findById(id).orElse(null);
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Not found", null);
        }

        return buildDesignRequestResponseForSchool(designRequest);
    }

    @Override
    public ResponseEntity<ResponseObject> getListDesignRequestByDesigner(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<DesignQuotation> quotations = account.getCustomer().getPartner().getDesignQuotations().stream().filter(quotation -> quotation.getStatus().equals(Status.DESIGN_QUOTATION_SELECTED)).toList();

        List<DesignRequest> designRequests = new ArrayList<>();

        for (DesignQuotation quotation : quotations) {
            designRequestRepo.findByDesignQuotationId(quotation.getId()).ifPresent(designRequests::add);
        }

        return buildDesignRequestResponseForDesigner(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> getDesignRequestDetailForDesigner(int id) {
        DesignRequest designRequest = designRequestRepo.findById(id).orElse(null);
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Not found", null);
        }

        return buildDesignRequestResponseForDesigner(designRequest);
    }

    @Override
    public ResponseEntity<ResponseObject> updateRequestByDeadline(UpdateRequestByDeadline request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "request not found", null);
        }

        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_PENDING)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Can not cancel or re-find request", null);
        }

        if (request.getType().equalsIgnoreCase("refind")) {
            designRequest.setCreationDate(LocalDate.now());
            designRequestRepo.save(designRequest);
            return ResponseBuilder.build(HttpStatus.OK, "Continue looking for designer for your request", null);
        }
        if (request.getType().equalsIgnoreCase("cancel")) {
            designRequest.setStatus(Status.DESIGN_REQUEST_CANCELED);
            designRequestRepo.save(designRequest);
            return ResponseBuilder.build(HttpStatus.OK, "Your request has been cancelled", null);
        }
        return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Fail to process", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> duplicateRequest(DuplicateRequest request) {

        DesignRequest oldDesign = designRequestRepo.findById(request.getId()).orElse(null);
        if (oldDesign == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Old design not found", null);
        }

        if (!oldDesign.getStatus().equals(Status.DESIGN_REQUEST_CANCELED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Old design are not canceled", null);
        }

        DesignRequest newDesign = DesignRequest.builder()
                .name(oldDesign.getName())
                .school(oldDesign.getSchool())
                .logoImage(oldDesign.getLogoImage())
                .creationDate(LocalDate.now())
                .status(Status.DESIGN_REQUEST_PENDING)
                .privacy(oldDesign.isPrivacy())
                .build();


        List<DesignItem> newItems = oldDesign.getDesignItems().stream()
                .map(item -> DesignItem.builder()
                        .designRequest(newDesign)
                        .fabric(item.getFabric())
                        .category(item.getCategory())
                        .color(item.getColor())
                        .gender(item.getGender())
                        .logoPosition(item.getLogoPosition())
                        .note(item.getNote())
                        .type(item.getType())
                        .build())
                .toList();

        newDesign.setDesignItems(newItems);

        designRequestRepo.save(newDesign);

        return ResponseBuilder.build(HttpStatus.CREATED, "Design duplicated successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> buyRevisionTime(UpdateRevisionTimeRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design request not found", null);
        }

        DesignQuotation designQuotation = designQuotationRepo.findById(designRequest.getDesignQuotationId()).orElse(null);
        if (designQuotation == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design quotation not found", null);
        }

        String message = BuyRevisionValidation.validate(designRequest,request, designQuotation);

        if(message != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, message, null);
        }

        designRequest.setRevisionTime(request.getRevisionTime());
        designRequestRepo.save(designRequest);

        return ResponseBuilder.build(HttpStatus.OK, "Buy revision successful", null);
    }

    //-----------------------------------FABRIC---------------------------------------//
    @Override
    public ResponseEntity<ResponseObject> getAllFabric() {

        List<Fabric> fabrics = fabricRepo.findAll();

        Map<String, Object> response = new HashMap<>();

        for (DesignItemCategory category : DesignItemCategory.values()) {
            List<Fabric> categoryFabric = fabrics.stream()
                    .filter(fabric -> fabric.getDesignItemCategory().equals(category))
                    .toList();

            Map<String, Object> categoryMap = new HashMap<>();

            List<Map<String, Object>> shirts = categoryFabric.stream()
                    .filter(f -> f.getDesignItemType().equals(DesignItemType.SHIRT))
                    .map(this::mapFabric)
                    .toList();

            List<Map<String, Object>> pants = categoryFabric.stream()
                    .filter(f -> f.getDesignItemType().equals(DesignItemType.PANTS))
                    .map(this::mapFabric)
                    .toList();

            List<Map<String, Object>> skirts = categoryFabric.stream()
                    .filter(f -> f.getDesignItemType().equals(DesignItemType.SKIRT))
                    .map(this::mapFabric)
                    .toList();

            categoryMap.put("shirts", shirts);
            categoryMap.put("pants", pants);
            categoryMap.put("skirts", skirts);

            response.put(category.name().toLowerCase(), categoryMap);

        }

        return ResponseBuilder.build(HttpStatus.OK, "list fabrics", response);
    }

    //---------------------------------DESIGN_DELIVERY--------------------------------//
    @Override
    public ResponseEntity<ResponseObject> getListDeliveries(GetListDeliveryRequest request) {

        List<DesignDelivery> deliveries = designDeliveryRepo.findAllByDesignRequest_Id(request.getDesignRequestId());

        List<Map<String, Object>> deliveriesMap = deliveries.stream().map(
                designDelivery -> {
                    Map<String, Object> delivery = new HashMap<>();
                    delivery.put("id", designDelivery.getId());
                    delivery.put("name", designDelivery.getName());
                    delivery.put("isRevision", designDelivery.isRevision());
                    delivery.put("submitDate", designDelivery.getSubmitDate());
                    delivery.put("note", designDelivery.getNote());
                    delivery.put("version", designDelivery.getVersion());
                    delivery.put("revisionRequest", EntityResponseBuilder.buildRevisionRequestResponse(designDelivery.getRevisionRequest()));
                    delivery.put("deliveryItems", EntityResponseBuilder.buildDeliveryItemListResponse(designDelivery.getDeliveryItems(), designItemRepo));
                    return delivery;
                }
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "list deliveries", deliveriesMap);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createNewDelivery(CreateNewDeliveryRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        RevisionRequest revisionRequest = revisionRequestRepo.findById(request.getRevisionId()).orElse(null);

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }

        int version = 1;
        DesignDelivery latestDelivery = designDeliveryRepo.findFirstByDesignRequest_IdOrderByIdDesc(designRequest.getId()).orElse(null);
        if (latestDelivery != null) {
            version = latestDelivery.getVersion() + 1;
        }

        if (request.isRevision() && designRequest.getRevisionTime() < 9999) {
            designRequest.setRevisionTime(designRequest.getRevisionTime() - 1);
            designRequest = designRequestRepo.save(designRequest);
        }

        DesignDelivery delivery = designDeliveryRepo.save(
                DesignDelivery.builder()
                        .designRequest(designRequest)
                        .revisionRequest(revisionRequest)
                        .name(request.getName())
                        .submitDate(LocalDate.now())
                        .revision(request.isRevision())
                        .note(request.getNote())
                        .version(version)
                        .build()
        );

        for (CreateNewDeliveryRequest.DeliveryItems i : request.getItemList()) {
            deliveryItemRepo.save(
                    DeliveryItem.builder()
                            .designDelivery(delivery)
                            .designItemId(i.getDesignItemId())
                            .baseLogoHeight(i.getLogoHeight())
                            .baseLogoWidth(i.getLogoWidth())
                            .backImageUrl(i.getBackUrl())
                            .frontImageUrl(i.getFrontUrl())
                            .build()
            );
        }
//
//        DesignComment designComment = DesignComment.builder()
//                .content("Designer has submit new delivery. Delivery Code: ")
//                .designRequest(designRequest)
//                .creationDate(LocalDateTime.now())
//                .senderId(0)
//                .senderRole("system")
//                .build();
//        designCommentRepo.save(designComment);

        return ResponseBuilder.build(HttpStatus.CREATED, "Upload delivery successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> addFileUrl(AddFileUrlRequest request) {

        DesignDelivery designDelivery = designDeliveryRepo.findById(request.getDeliveryId()).orElse(null);

        if (designDelivery == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "delivery not found", null);
        }

        designDelivery.setFileUrl(request.getFileUrl());
        designDeliveryRepo.save(designDelivery);

        return ResponseBuilder.build(HttpStatus.CREATED, "Upload file successfully", null);
    }

    //-----------------------REVISION_REQUEST-------------------------//
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createRevisionRequest(CreateRevisionRequest request) {

        DesignDelivery designDelivery = designDeliveryRepo.findById(request.getDeliveryId()).orElse(null);

        if (designDelivery == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "No delivery found to create a revision", null);
        }

        DesignRequest designRequest = designDelivery.getDesignRequest();

        if (designRequest.getRevisionTime() <= 0) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "No more revision time", null);
        }

        for (DesignDelivery delivery : designRequest.getDesignDeliveries()) {
            if (delivery.getSchoolDesign() != null) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This design request is already completed", null);
            }

            for (RevisionRequest revisionRequest : delivery.getRevisionRequests()) {
                if (revisionRequest.getResultDelivery() == null) {
                    return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "There is at least a revision is proceed", null);
                }
            }
        }

        RevisionRequest revisionRequest = RevisionRequest.builder()
                .designDelivery(designDelivery)
                .requestDate(LocalDate.now())
                .note(request.getNote())
                .build();
//
//        assert customer != null;
//        DesignComment designComment = DesignComment.builder()
//                .content(" School  " + customer.getName() + "has sent a revision request ")
//                .designRequest(designDelivery.getDesignRequest())
//                .creationDate(LocalDateTime.now())
//                .senderId(0)
//                .senderRole("system")
//                .build();

//        designCommentRepo.save(designComment);

        revisionRequestRepo.save(revisionRequest);

        return ResponseBuilder.build(HttpStatus.CREATED, "Revision request sent", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(GetUnUseListRevisionRequest request) {
        List<Map<String, Object>> revisionRequestList = designDeliveryRepo.findAllByDesignRequest_Id(request.getRequestId())
                .stream()
                .map(DesignDelivery::getRevisionRequests)
                .flatMap(List::stream)
                .filter(revisionRequest -> revisionRequest.getResultDelivery() == null)
                .map(revisionRequest -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", revisionRequest.getId());
                    map.put("deliveryId", revisionRequest.getDesignDelivery().getId());
                    map.put("requestDate", revisionRequest.getRequestDate());
                    map.put("note", revisionRequest.getNote());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("List revision request not completed")
                        .body(revisionRequestList)
                        .build()
        );
    }

    //-----------------------DESIGN_COMMENT-------------------------//
    @Override
    public ResponseEntity<ResponseObject> getListDesignComment(GetListCommentRequest request) {

        List<DesignComment> designComments = designCommentRepo.findAllByDesignRequest_Id(request.getRequestId());


        List<Map<String, Object>> mapList = designComments.stream()
                .map(
                        comment -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("senderId", comment.getSenderId());
                            map.put("senderRole", comment.getSenderRole());
                            map.put("content", comment.getContent());
                            map.put("createdAt", comment.getCreationDate());
                            return map;
                        }
                ).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List of Design Comments")
                        .body(mapList)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> sendComment(HttpServletRequest request, SendCommentRequest sendCommentRequest) {

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        DesignRequest designRequest = designRequestRepo.findById(sendCommentRequest.getRequestId()).orElse(null);
        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find design request")
                            .build()
            );
        }

        List<DesignDelivery> deliveries = designDeliveryRepo.findAllByDesignRequest_Id(designRequest.getId());

        boolean isFinal = deliveries.stream().anyMatch(delivery ->
                schoolDesignRepo.existsByCustomer_IdAndDesignDelivery_Id(
                        designRequest.getSchool().getId(),
                        delivery.getId()
                )
        );

        if (isFinal) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST,
                    "This request has a final delivery. No more comments allowed.", null);
        }

        DesignComment comment = DesignComment.builder()
                .designRequest(designRequest)
                .content(sendCommentRequest.getComment())
                .creationDate(LocalDateTime.now())
                .senderId(account.getId())
                .senderRole(account.getRole().getValue())
                .build();

        designCommentRepo.save(comment);

        return ResponseBuilder.build(HttpStatus.CREATED, "Comment sent", null);
    }

    //-----------------------SCHOOL_DESIGN-------------------------//
    @Override
    public ResponseEntity<ResponseObject> getListSchoolDesign(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);


        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<SchoolDesign> schoolDesigns;

        if (account.getRole().equals(Role.SCHOOL)) {
            schoolDesigns = account.getCustomer().getSchoolDesigns();
        } else {
            schoolDesigns = account.getCustomer().getPartner().getDesignQuotations().stream()
                    .filter(quotation -> quotation.getStatus().equals(Status.DESIGN_QUOTATION_SELECTED))
                    .map(DesignQuotation::getDesignRequest)
                    .map(DesignRequest::getDesignDeliveries)
                    .flatMap(List::stream)
                    .map(DesignDelivery::getSchoolDesign)
                    .filter(Objects::nonNull)
                    .toList();
        }

        return ResponseBuilder.build(HttpStatus.OK, "", EntityResponseBuilder.buildSchoolDesignListResponse(schoolDesigns, designItemRepo));
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> makeDesignFinal(HttpServletRequest httpRequest, MakeDesignFinalRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        DesignDelivery designDelivery = designDeliveryRepo.findById(request.getDeliveryId()).orElse(null);

        if (designDelivery == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No design delivery found", null);
        }

        schoolDesignRepo.save(SchoolDesign.builder()
                .designDelivery(designDelivery)
                .customer(account.getCustomer())
                .build());

        designDelivery.getDesignRequest().setStatus(Status.DESIGN_REQUEST_COMPLETED);
        designDeliveryRepo.save(designDelivery);

        return ResponseBuilder.build(HttpStatus.CREATED, "Design finished", null);
    }

    //-----------------------DESIGN_QUOTATION-------------------------//
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> pickDesignQuotation(PickDesignQuotationRequest request, HttpServletRequest httpRequest) {

        DesignQuotation designQuotation = designQuotationRepo.findById(request.getDesignQuotationId()).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designQuotation == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Quotation not found", null);
        }
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Request not found", null);
        }
        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_PENDING)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Package already exists for this request", null);
        }

        designRequest.setDesignQuotationId(request.getDesignQuotationId());
        designRequest.setRevisionTime(designQuotation.getRevisionTime() + request.getExtraRevision());
        designRequest.setStatus(Status.DESIGN_REQUEST_PROCESSING);
        designRequest.setPrice(designQuotation.getPrice() + request.getServiceFee() + designQuotation.getExtraRevisionPrice() * request.getExtraRevision());
        designRequestRepo.save(designRequest);

        designQuotation.setStatus(Status.DESIGN_QUOTATION_SELECTED);
        designQuotation = designQuotationRepo.save(designQuotation);

        for (DesignQuotation quotation : designRequest.getDesignQuotations()) {
            if (!Objects.equals(quotation.getId(), designQuotation.getId())) {
                quotation.setStatus(Status.DESIGN_QUOTATION_REJECTED);
                designQuotationRepo.save(quotation);
            }
        }

        Partner designer = partnerRepo.findById(request.getCreateTransactionRequest().getReceiverId()).orElse(null);
        if(designer == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Designer not found", null);

        request.getCreateTransactionRequest().setReceiverId(designer.getCustomer().getId());

        return paymentService.createTransaction(request.getCreateTransactionRequest(), httpRequest);
    }

    @Override
    public ResponseEntity<ResponseObject> getQuotationHistory(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<DesignQuotation> designQuotationList = designQuotationRepo.findAllByDesigner_Customer_Account_Id(account.getId());

        List<Map<String, Object>> designQuotation = designQuotationList.stream().map(
                quotation -> {
                    List<String> keys = List.of(
                            "id", "designRequest",
                            "note", "deliveryWithIn",
                            "revisionTime", "extraRevisionPrice",
                            "price", "acceptanceDeadline",
                            "status"
                    );
                    List<Object> values = List.of(
                            quotation.getId(), EntityResponseBuilder.buildDesignRequestResponse(quotation.getDesignRequest()),
                            quotation.getNote(), quotation.getDeliveryWithIn(),
                            quotation.getRevisionTime(), quotation.getExtraRevisionPrice(),
                            quotation.getPrice(), quotation.getAcceptanceDeadline(),
                            quotation.getStatus()
                    );
                    return MapUtils.build(keys, values);
                }
        ).toList();
        return ResponseBuilder.build(HttpStatus.OK, "", designQuotation);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpRequest, CreateDesignQuotationRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design request not found", null);
        }

        if (designQuotationRepo.existsByDesigner_Customer_Account_IdAndDesignRequest_Id(account.getId(), designRequest.getId())) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "You already request to apply this design", null);
        }

        designQuotationRepo.save(DesignQuotation.builder()
                .designer(account.getCustomer().getPartner())
                .designRequest(designRequest)
                .note(request.getNote())
                .deliveryWithIn(request.getDeliveryWithIn())
                .revisionTime(request.getRevisionTime())
                .extraRevisionPrice(request.getExtraRevisionPrice())
                .price(request.getPrice())
                .acceptanceDeadline(request.getAcceptanceDeadline())
                .status(Status.DESIGN_QUOTATION_PENDING)
                .build());

        return ResponseBuilder.build(HttpStatus.CREATED, "Design quotation created", null);
    }

    //-----------------------PRIVATE-------------------------//
    private Map<String, Object> mapFabric(Fabric fabric) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", fabric.getId());
        map.put("name", fabric.getName());
        map.put("description", fabric.getDescription());
        return map;
    }

    private void createSampleImageByItem(DesignItem designItem, List<CreateDesignRequest.Item.Image> images) {
        for (CreateDesignRequest.Item.Image image : images) {
            sampleImageRepo.save(
                    SampleImage.builder()
                            .imageUrl(image.getUrl())
                            .designItem(designItem)
                            .build());
        }
    }

    private ResponseEntity<ResponseObject> buildDesignRequestResponseForDesigner(List<DesignRequest> designRequests) {
        List<Map<String, Object>> designRequestMaps = designRequests.stream().map(
                EntityResponseBuilder::buildDesignRequestResponse
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", designRequestMaps);
    }

    private ResponseEntity<ResponseObject> buildDesignRequestResponseForDesigner(DesignRequest designRequest) {
        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", EntityResponseBuilder.buildDesignRequestResponse(designRequest));
    }

    private ResponseEntity<ResponseObject> buildDesignRequestResponseForSchool(List<DesignRequest> designRequests) {
        List<Map<String, Object>> designRequestMaps = designRequests.stream().map(
                designRequest -> {
                    boolean completed = designRequest.getStatus().equals(Status.DESIGN_REQUEST_COMPLETED);

                    List<String> keys = List.of(
                            "id",
                            "feedback",
                            "finalDesignQuotation",
                            "designQuotations",
                            "name", "creationDate", "logoImage",
                            "privacy", "status", "items",
                            "revisionTime", "price"
                    );

                    if (completed) {
                        keys = List.of(
                                "id",
                                "feedback",
                                "finalDesignQuotation",
                                "designQuotations",
                                "name", "creationDate", "logoImage",
                                "privacy", "status", "items",
                                "revisionTime", "price",
                                "resultDelivery"
                        );
                    }

                    List<Object> values;

                    if (designRequest.getDesignQuotationId() == null) {
                        Feedback feedback = designRequest.getFeedback();
                        values = List.of(
                                designRequest.getId(),
                                feedback != null ? EntityResponseBuilder.buildFeedbackResponse(feedback) : "",
                                "",
                                EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations()),
                                designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                                designRequest.isPrivacy(), designRequest.getStatus().getValue(), EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()),
                                designRequest.getRevisionTime(), designRequest.getPrice()
                        );
                    } else {
                        DesignQuotation quotation = designQuotationRepo.findById(designRequest.getDesignQuotationId()).orElse(null);

                        if (quotation == null) {
                            Feedback feedback = designRequest.getFeedback();
                            values = List.of(
                                    designRequest.getId(),
                                    feedback != null ? EntityResponseBuilder.buildFeedbackResponse(feedback) : "",
                                    "",
                                    EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations()),
                                    designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                                    designRequest.isPrivacy(), designRequest.getStatus().getValue(), EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()),
                                    designRequest.getRevisionTime(), designRequest.getPrice()
                            );
                        } else {
                            Feedback feedback = designRequest.getFeedback();
                            values = List.of(
                                    designRequest.getId(),
                                    feedback != null ? EntityResponseBuilder.buildFeedbackResponse(feedback) : "",
                                    EntityResponseBuilder.buildDesignQuotationResponse(quotation),
                                    EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations()),
                                    designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                                    designRequest.isPrivacy(), designRequest.getStatus().getValue(), EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()),
                                    designRequest.getRevisionTime(), designRequest.getPrice()
                            );

                            if (completed) {
                                values = List.of(
                                        designRequest.getId(),
                                        feedback != null ? EntityResponseBuilder.buildFeedbackResponse(feedback) : "",
                                        EntityResponseBuilder.buildDesignQuotationResponse(quotation),
                                        EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations()),
                                        designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                                        designRequest.isPrivacy(), designRequest.getStatus().getValue(), EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()),
                                        designRequest.getRevisionTime(), designRequest.getPrice(),
                                        buildResultDeliveryResponse(designRequest)
                                );
                            }
                        }
                    }

                    return MapUtils.build(keys, values);
                }
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", designRequestMaps);
    }

    private ResponseEntity<ResponseObject> buildDesignRequestResponseForSchool(DesignRequest designRequest) {
        List<String> keys = List.of(
                "id",
                "feedback",
                "finalDesignQuotation",
                "designQuotations",
                "name", "creationDate", "logoImage",
                "privacy", "status", "items",
                "revisionTime", "price"
        );

        List<Object> values;

        if (designRequest.getDesignQuotationId() == null) {
            Feedback feedback = designRequest.getFeedback();
            values = List.of(
                    designRequest.getId(),
                    Objects.requireNonNullElse(EntityResponseBuilder.buildFeedbackResponse(feedback), ""),
                    "",
                    EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations()),
                    designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                    designRequest.isPrivacy(), designRequest.getStatus().getValue(), EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()),
                    designRequest.getRevisionTime(), designRequest.getPrice()
            );
        } else {
            DesignQuotation quotation = designQuotationRepo.findById(designRequest.getDesignQuotationId()).orElse(null);

            if (quotation == null) {
                Feedback feedback = designRequest.getFeedback();
                values = List.of(
                        designRequest.getId(),
                        Objects.requireNonNullElse(EntityResponseBuilder.buildFeedbackResponse(feedback), ""),
                        "",
                        EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations()),
                        designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                        designRequest.isPrivacy(), designRequest.getStatus().getValue(), EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()),
                        designRequest.getRevisionTime(), designRequest.getPrice()
                );
            } else {
                Feedback feedback = designRequest.getFeedback();
                values = List.of(
                        designRequest.getId(),
                        Objects.requireNonNullElse(EntityResponseBuilder.buildFeedbackResponse(feedback), ""),
                        EntityResponseBuilder.buildDesignQuotationResponse(quotation),
                        EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations()),
                        designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                        designRequest.isPrivacy(), designRequest.getStatus().getValue(), EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()),
                        designRequest.getRevisionTime(), designRequest.getPrice()
                );
            }
        }

        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", MapUtils.build(keys, values));
    }

    //-------Result Delivery---------
    private Map<String, Object> buildResultDeliveryResponse(DesignRequest request) {
        if (request == null || !request.getStatus().equals(Status.DESIGN_REQUEST_COMPLETED)) return null;
        SchoolDesign design = schoolDesignRepo.findByDesignDelivery_DesignRequest_Id(request.getId()).orElse(null);
        assert design != null;
        DesignDelivery delivery = design.getDesignDelivery();

        List<String> keys = List.of(
                "id",
                "name",
                "submitDate",
                "note",
                "items"
        );

        List<Object> values = List.of(
                delivery.getId(),
                delivery.getName(),
                delivery.getSubmitDate(),
                delivery.getNote() == null ? "" : delivery.getNote(),
                EntityResponseBuilder.buildDeliveryItemListResponse(delivery.getDeliveryItems(), designItemRepo)
        );

        return MapUtils.build(keys, values);
    }

}
