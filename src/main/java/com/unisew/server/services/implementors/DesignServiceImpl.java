package com.unisew.server.services.implementors;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Gender;
import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import com.unisew.server.services.JWTService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.MapUtils;
import com.unisew.server.utils.ResponseBuilder;
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
    private final CustomerRepo customerRepo;


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
                .status(Status.DESIGN_REQUEST_CREATED)
                .privacy(true)
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
                .filter(designRequest -> designRequest.getStatus().equals(Status.DESIGN_REQUEST_CREATED))
                .toList();

        return buildDesignRequestResponseForDesigner(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> getListDesignRequestByCustomer(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<DesignRequest> designRequests = designRequestRepo.findAllBySchool_Id(account.getCustomer().getId());

        return buildDesignRequestResponseForSchool(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> updateRequestByDeadline(UpdateRequestByDeadline request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "request not found", null);
        }

        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_CREATED)) {
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
                .status(Status.DESIGN_REQUEST_CREATED)
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

    //----------------------------------RequestReceipt---------------------------//
    @Override
    public ResponseEntity<ResponseObject> getListReceipt(GetListReceiptRequest request) {
//        List<RequestReceipt> receipts = requestReceiptRepo.findAllByDesignRequest_Id(request.getDesignRequestId());
//
//        Map<String, Map<String, Object>> designerMap = new LinkedHashMap<>();
//
//        for (RequestReceipt r : receipts) {
//
//            Integer designerId = r.getPkg().getDesigner().getId();
//            String designerName = r.getPkg().getDesigner().getCustomer().getName();
//
//            if (!designerMap.containsKey(designerName)) {
//                Map<String, Object> designerObj = new HashMap<>();
//                designerObj.put("designerId", designerId);
//                designerObj.put("designerName", designerName);
//                designerObj.put("rating", r.getPkg().getDesigner().getRating());
//                designerObj.put("email", r.getPkg().getDesigner().getCustomer().getAccount().getEmail());
//                designerObj.put("phone", r.getPkg().getDesigner().getCustomer().getPhone());
//                designerObj.put("completeProject", schoolDesignRepo.findAllByDesignDelivery_DesignRequest_PackageId(r.getPkg().getId()).size());
//                designerObj.put("acceptance", r.getAcceptanceDeadline());
//                designerObj.put("status", r.getStatus());
//                designerObj.put("packages", new ArrayList<Map<String, Object>>());
//                designerMap.put(designerName, designerObj);
//            }
//
//            Map<String, Object> designerObj = designerMap.get(designerName);
//            List<Map<String, Object>> packages = (List<Map<String, Object>>) designerObj.get("packages");
//
//            Map<String, Object> pkgObj = new HashMap<>();
//            pkgObj.put("id", r.getPkg().getId());
//            pkgObj.put("name", r.getPkg().getName());
//            pkgObj.put("pkgHeaderContent", r.getPkg().getHeaderContent());
//            pkgObj.put("pkgDuration", r.getPkg().getDeliveryDuration());
//            pkgObj.put("pkgRevisionTime", r.getPkg().getRevisionTime());
//            pkgObj.put("pkgFee", r.getPkg().getFee());
//
//
//            packages.add(pkgObj);
//        }
//
//        List<Map<String, Object>> result = new ArrayList<>(designerMap.values());
        List<Map<String, Object>> result = new ArrayList<>();

        return ResponseBuilder.build(HttpStatus.OK, "list grouped receipt", result);
    }

    @Override
    public ResponseEntity<ResponseObject> addPackageToReceipt(AddPackageToReceiptRequest request) {


        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }

//        List<RequestReceipt> requestReceiptList = requestReceiptRepo.findAllByDesignRequest_Id(request.getDesignRequestId());
//        List<RequestReceipt> requestReceiptList = null;

        List<Integer> addedPackages = new ArrayList<>();
        List<Integer> skippedPackages = new ArrayList<>();

        for (Integer pkgId : request.getPackageId()) {
            DesignQuotation designQuotation = designQuotationRepo.findById(pkgId).orElse(null);
            if (designQuotation == null) {
                skippedPackages.add(pkgId);
            }
//            boolean alreadyExists = requestReceiptList.stream()
//                    .anyMatch(r -> r.getPkg().getId().equals(pkgId));
            boolean alreadyExists = true;

            if (alreadyExists) {
                skippedPackages.add(pkgId);
            }
//            RequestReceipt requestReceipt = RequestReceipt.builder()
//                    .pkg(packages)
//                    .designRequest(designRequest)
//                    .acceptanceDeadline(request.getAcceptanceDeadline())
//                    .status(Status.RECEIPT_PENDING)
//                    .build();

//            requestReceiptRepo.save(requestReceipt);
            addedPackages.add(pkgId);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Offer successfully", null);
    }

    //---------------------------------DESIGN_DELIVERY--------------------------------//
    @Override
    public ResponseEntity<ResponseObject> getListDeliveries(GetListDeliveryRequest request) {

        List<DesignDelivery> deliveries = designDeliveryRepo.findAllByDesignRequest_Id(request.getDesignRequestId());

        List<Map<String, Object>> deliveriesMap = deliveries.stream().map(
                designDelivery -> {
                    Map<String, Object> delivery = new HashMap<>();
                    delivery.put("id", designDelivery.getId());
                    delivery.put("code", designDelivery.getCode());
                    delivery.put("isRevision", designDelivery.isRevision());
                    delivery.put("submitDate", designDelivery.getSubmitDate());
                    delivery.put("note", designDelivery.getNote());
                    List<Map<String, Object>> revisionMap = designDelivery.getRevisionRequests().stream().map(
                            revisionRequest -> {
                                Map<String, Object> revision = new HashMap<>();
                                revision.put("id", revisionRequest.getId());
                                revision.put("requestDate", revisionRequest.getRequestDate());
                                revision.put("note", revisionRequest.getNote());
                                return revision;
                            }
                    ).toList();
                    delivery.put("revisionRequests", revisionMap);
                    return delivery;
                }
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "list deliveries", deliveriesMap);
    }

    @Override
    public ResponseEntity<ResponseObject> createNewDelivery(CreateNewDeliveryRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        RevisionRequest revisionRequest = revisionRequestRepo.findById(request.getRevisionId()).orElse(null);

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }


        Optional<DesignDelivery> latestDelivery = designDeliveryRepo.findTopByDesignRequest_IdOrderByCodeDesc(request.getDesignRequestId());
        int nextDeliveryCode = latestDelivery.map(d -> d.getCode() + 1).orElse(1);


        DesignDelivery delivery = DesignDelivery.builder()
                .code(nextDeliveryCode)
                .revision(request.isRevision())
                .designRequest(designRequest)
                .revisionRequest(revisionRequest)
                .submitDate(LocalDate.now())
                .note(request.getNote())
                .build();

        designDeliveryRepo.save(delivery);

        for (CreateNewDeliveryRequest.DeliveryItems i : request.getItemList()) {
            DeliveryItem deliveryItem = DeliveryItem.builder()
                    .baseLogoHeight(i.getLogoHeight())
                    .baseLogoWidth(i.getLogoWidth())
                    .designDelivery(delivery)
                    .designItemId(i.getDesignItemId())
                    .backImageUrl(i.getBackUrl())
                    .frontImageUrl(i.getFrontUrl())
                    .build();
            deliveryItemRepo.save(deliveryItem);
        }

        DesignComment designComment = DesignComment.builder()
                .content("Designer has submit new delivery. Delivery Code: " + delivery.getCode())
                .designRequest(designRequest)
                .creationDate(LocalDateTime.now())
                .senderId(0)
                .senderRole("system")
                .build();
        designCommentRepo.save(designComment);

        return ResponseBuilder.build(HttpStatus.CREATED, "delivery has been created", null);
    }

    //-----------------------REVISION_REQUEST-------------------------//
    @Override
    public ResponseEntity<ResponseObject> createRevisionRequest(CreateRevisionRequest request) {

        DesignDelivery designDelivery = designDeliveryRepo.findById(request.getDeliveryId()).orElse(null);


        if (designDelivery == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "No delivery found to create a revision", null);
        }

        Customer customer = customerRepo.findById(designDelivery.getDesignRequest().getSchool().getId()).orElse(null);

        boolean exitSchoolDesign = schoolDesignRepo.existsByCustomer_IdAndDesignDelivery_Id(designDelivery.getDesignRequest().getSchool().getId(), designDelivery.getId());

        if (exitSchoolDesign) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This delivery already final", null);
        }

        RevisionRequest revisionRequest = RevisionRequest.builder()
                .designDelivery(designDelivery)
                .requestDate(LocalDate.now())
                .note(request.getNote())
                .build();

        assert customer != null;
        DesignComment designComment = DesignComment.builder()
                .content(" School  " + customer.getName() + "has sent a revision request ")
                .designRequest(designDelivery.getDesignRequest())
                .creationDate(LocalDateTime.now())
                .senderId(0)
                .senderRole("system")
                .build();

        designCommentRepo.save(designComment);

        revisionRequestRepo.save(revisionRequest);

        return ResponseBuilder.build(HttpStatus.CREATED, "revision request has been created", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(GetUnUseListRevisionRequest request) {

        List<DesignDelivery> designDeliveryList = designDeliveryRepo.findAllByDesignRequest_Id(request.getRequestId());

        List<RevisionRequest> revisionRequestList = revisionRequestRepo.findAllByDesignDelivery_DesignRequest_Id(request.getRequestId());

        if (revisionRequestList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("No revision request for this request")
                            .build()
            );
        }

        Set<Integer> usedRevisionIds = designDeliveryList.stream()
                .map(DesignDelivery::getRevisionRequest)
                .filter(Objects::nonNull)
                .map(RevisionRequest::getId)
                .collect(Collectors.toSet());


        List<RevisionRequest> unusedRevisionRequests = revisionRequestList.stream()
                .filter(rev -> !usedRevisionIds.contains(rev.getId()))
                .toList();

        List<Map<String, Object>> mapList = unusedRevisionRequests.stream()
                .map(
                        revisionRequest -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", revisionRequest.getId());
                            map.put("deliveryId", revisionRequest.getDesignDelivery().getId());
                            map.put("requestDate", revisionRequest.getRequestDate());
                            map.put("note", revisionRequest.getNote());
                            return map;
                        }
                ).toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("List revision request not completed")
                        .body(mapList)
                        .build()
        );
    }

    //-----------------------REVISION_REQUEST-------------------------//
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
    public ResponseEntity<ResponseObject> getListSchoolDesign(HttpServletRequest httpRequest, GetListSchoolDesignRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);


        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<SchoolDesign> schoolDesigns = schoolDesignRepo.findAllByCustomer_Account_Id(account.getId());

        List<Map<String, Object>> mapList = schoolDesigns.stream().map(
                schoolDesign -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", schoolDesign.getId());

                    Map<String, Object> deliveryMap = new HashMap<>();
                    deliveryMap.put("deliveryId", schoolDesign.getDesignDelivery().getId());
                    deliveryMap.put("deliveryCode", schoolDesign.getDesignDelivery().getCode());

                    List<Map<String, Object>> itemList = schoolDesign.getDesignDelivery().getDeliveryItems()
                            .stream().map(
                                    deliveryItem -> {
                                        Map<String, Object> item = new HashMap<>();
                                        item.put("id", deliveryItem.getId());
                                        item.put("baseWidth", deliveryItem.getBaseLogoWidth());
                                        item.put("baseHeight", deliveryItem.getBaseLogoHeight());
                                        item.put("backUrl", deliveryItem.getBackImageUrl());
                                        item.put("frontUrl", deliveryItem.getFrontImageUrl());

                                        DesignItem designItem = designItemRepo.findById(deliveryItem.getDesignItemId()).orElse(null);
                                        assert designItem != null;
                                        item.put("itemType", designItem.getType().getValue());
                                        item.put("fabric", designItem.getFabric().getName());
                                        item.put("category", designItem.getCategory().getValue());
                                        item.put("gender", designItem.getGender().getValue());
                                        item.put("logoPosition", designItem.getLogoPosition());
                                        return item;
                                    }
                            ).toList();
                    deliveryMap.put("items", itemList);
                    map.put("delivery", deliveryMap);
                    return map;
                }
        ).toList();
        return ResponseBuilder.build(HttpStatus.OK, "List of School Designs", mapList);
    }

    @Override
    public ResponseEntity<ResponseObject> makeDesignFinal(HttpServletRequest httpRequest, MakeDesignFinalRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        DesignDelivery designDelivery = designDeliveryRepo.findById(request.getDeliveryId()).orElse(null);

        if (designDelivery == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "No design delivery found", null);
        }

        SchoolDesign schoolDesign = SchoolDesign.builder()
                .designDelivery(designDelivery)
                .customer(account.getCustomer())
                .build();

        schoolDesignRepo.save(schoolDesign);

        return ResponseBuilder.build(HttpStatus.CREATED, "Design finished", null);
    }

    //-----------------------DESIGN QUOTATION-------------------------//
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> pickDesignQuotation(PickDesignQuotationRequest request) {

        DesignQuotation designQuotation = designQuotationRepo.findById(request.getDesignQuotationId()).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designQuotation == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "package not found", null);
        }
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }
        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_CREATED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Package already exists for this request", null);
        }

        designRequest.setDesignQuotationId(request.getDesignQuotationId());
        designRequest.setRevisionTime(designQuotation.getRevisionTime() + request.getExtraRevision());
        designRequest.setStatus(Status.DESIGN_REQUEST_PAID);
        designRequestRepo.save(designRequest);

        designQuotation.setStatus(Status.DESIGN_QUOTATION_SELECTED);
        designQuotation = designQuotationRepo.save(designQuotation);

        for(DesignQuotation quotation: designRequest.getDesignQuotations()){
            if(!Objects.equals(quotation.getId(), designQuotation.getId())){
                quotation.setStatus(Status.DESIGN_QUOTATION_REJECTED);
                designQuotationRepo.save(quotation);
            }
        }

        return ResponseBuilder.build(HttpStatus.OK, "Selected Designer", null);
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
                            quotation.getId(), buildDesignRequestResponse(quotation.getDesignRequest()),
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
        if(designRequest == null){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design request not found", null);
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
                this::buildDesignRequestResponse
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", designRequestMaps);
    }

    private ResponseEntity<ResponseObject> buildDesignRequestResponseForSchool(List<DesignRequest> designRequests) {
        List<Map<String, Object>> designRequestMaps = designRequests.stream().map(
                designRequest -> {
                    List<String> keys = List.of(
                            "id", "feedback", "designQuotation",
                            "name", "creationDate", "logoImage",
                            "privacy", "status", "items"
                    );

                    DesignQuotation quotation = designQuotationRepo.findById(designRequest.getDesignQuotationId()).orElse(null);

                    if(quotation == null){
                        return null;
                    }

                    List<Object> values = List.of(
                            designRequest.getId(), buildFeedbackResponse(designRequest.getFeedback()), buildDesignQuotationResponse(quotation),
                            designRequest.getName(), designRequest.getCreationDate(), designRequest.getLogoImage(),
                            designRequest.isPrivacy(), designRequest.getStatus().getValue(), buildDesignItemResponse(designRequest.getDesignItems())
                    );

                    return MapUtils.build(keys, values);
                }
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", designRequestMaps);
    }

    private Map<String, Object> buildFeedbackResponse(Feedback feedback) {
        List<String> keys = List.of("id", "rating", "content", "creationDate", "images");
        List<Object> values = List.of(feedback.getId(), feedback.getRating(), feedback.getContent(), feedback.getCreationDate(), buildFeedbackImageResponse(feedback.getFeedbackImages()));
        return MapUtils.build(keys, values);
    }

    private List<Map<String, Object>> buildFeedbackImageResponse(List<FeedbackImage> images) {
        return images.stream()
                .map(image -> {
                    List<String> keys = List.of("id", "url");
                    List<Object> values = List.of(image.getId(), image.getImageUrl());
                    return MapUtils.build(keys, values);
                }).toList();
    }

    private List<Map<String, Object>> buildDesignItemResponse(List<DesignItem> items) {
        return items.stream()
                .map(item -> {
                    List<String> keys = List.of(
                            "id", "type", "category", "logoPosition",
                            "color", "note", "sampleImages", "fabricId",
                            "fabricName"
                    );
                    List<Object> values = List.of(
                            item.getId(), item.getType().getValue(), item.getCategory().getValue(), item.getLogoPosition(),
                            item.getColor(), item.getNote(), buildSampleImageResponse(item.getSampleImages()), item.getFabric().getId(),
                            item.getFabric().getName()
                    );
                    return MapUtils.build(keys, values);
                }).toList();
    }

    private List<Map<String, Object>> buildSampleImageResponse(List<SampleImage> images) {
        return images.stream()
                .map(image -> {
                    List<String> keys = List.of("id", "url");
                    List<Object> values = List.of(image.getId(), image.getImageUrl());
                    return MapUtils.build(keys, values);
                })
                .toList();
    }

    private Map<String, Object> buildDesignQuotationResponse(DesignQuotation quotation){
        List<String> keys = List.of(
                "id", "designer", "note",
                "deliveryWithIn", "revisionTime",
                "extraRevisionPrice", "price",
                "acceptanceDeadline", "status"
        );
        List<Object> values = List.of(
                quotation.getId(), buildDesignerResponse(quotation.getDesigner()), quotation.getNote(),
                quotation.getDeliveryWithIn(), quotation.getRevisionTime(),
                quotation.getExtraRevisionPrice(), quotation.getPrice(),
                quotation.getAcceptanceDeadline(), quotation.getStatus().getValue()

        );
        return MapUtils.build(keys, values);
    }

    private Map<String, Object> buildDesignerResponse(Partner designer){
        List<String> keys = List.of(
                "id", "customer",
                "preview",
                "startTime", "endTime",
                "rating", "busy", "thumbnails"
        );
        List<Object> values = List.of(
                designer.getId(), buildCustomerResponse(designer.getCustomer()),
                designer.getInsidePreview(),
                designer.getStartTime(), designer.getEndTime(),
                designer.getRating(), designer.isBusy(), buildThumbnailImageResponse(designer.getThumbnailImages())
        );

        return MapUtils.build(keys, values);
    }

    private Map<String, Object> buildCustomerResponse(Customer customer){
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

    private Map<String, Object> buildAccountResponse(Account account){
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

    private Map<String, Object> buildDesignRequestResponse(DesignRequest request){
        List<String> keys = List.of(
                "id", "schoolId", "school",
                "name", "creationDate", "logoImage",
                "privacy", "status", "items"
        );
        List<Object> values = List.of(
                request.getId(), request.getSchool().getId(), request.getSchool().getBusinessName(),
                request.getName(), request.getCreationDate(), request.getLogoImage(),
                request.isPrivacy(), request.getStatus().getValue(), buildDesignItemResponse(request.getDesignItems())
        );

        return MapUtils.build(keys, values);
    }

    private List<Map<String, Object>> buildThumbnailImageResponse(List<ThumbnailImage> images){
        return images.stream()
                .map(image -> {
                    List<String> keys = List.of("id", "url");
                    List<Object> values = List.of(image.getId(), image.getImageUrl());
                    return MapUtils.build(keys, values);
                })
                .toList();
    }
}
