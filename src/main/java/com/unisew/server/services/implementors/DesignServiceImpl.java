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
    private final PackagesRepo packagesRepo;
    private final RequestReceiptRepo requestReceiptRepo;
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

        List<DesignRequest> designRequests = designRequestRepo.findAll();

        return getResponseObjectResponseEntity(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> getListDesignRequestByCustomer(HttpServletRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<DesignRequest> designRequests = designRequestRepo.findAllBySchool_Id(account.getCustomer().getId());

        return getResponseObjectResponseEntity(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> pickPackage(PickPackageRequest request) {

        Packages packages = packagesRepo.findById(request.getPackageId()).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (packages == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "package not found", null);
        }
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }
        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_CREATED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "package already exists for this request", null);
        }

        designRequest.setPackageId(request.getPackageId());
        designRequest.setPackagePrice(packages.getFee());
        designRequest.setPackageName(packages.getName());
        designRequest.setHeaderContent(packages.getHeaderContent());
        designRequest.setPackageDeliveryWithin(packages.getDeliveryDuration());
        designRequest.setRevisionTime(packages.getRevisionTime());
        designRequest.setStatus(Status.DESIGN_REQUEST_PENDING);
        designRequestRepo.save(designRequest);

        return ResponseBuilder.build(HttpStatus.OK, "pick package successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> updateRequestByDeadline(UpdateRequestByDeadline request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "request not found", null);
        }

        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_CREATED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Can not cancel or re find request", null);
        }

        if (request.getType().equalsIgnoreCase("refind")) {
            designRequest.setCreationDate(LocalDate.now());
            designRequestRepo.save(designRequest);
            return ResponseBuilder.build(HttpStatus.OK, "Continue looking for designer for your request", null);
        }
        if (request.getType().equalsIgnoreCase("cancel")) {
            designRequest.setStatus(Status.DESIGN_REQUEST_CANCEL);
            designRequestRepo.save(designRequest);
            return ResponseBuilder.build(HttpStatus.OK, "Your request has been cancelled", null);
        }
        return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "type must be cancel or refind", null);
    }

    @Override
    public ResponseEntity<ResponseObject> duplicateRequest(DuplicateRequest request) {

        DesignRequest oldDesign = designRequestRepo.findById(request.getId()).orElse(null);
        if (oldDesign == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Old design not found", null);
        }

        if (!oldDesign.getStatus().equals(Status.DESIGN_REQUEST_CANCEL)) {
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
        List<RequestReceipt> receipts = requestReceiptRepo.findAllByDesignRequest_Id(request.getDesignRequestId());

        Map<String, Map<String, Object>> designerMap = new LinkedHashMap<>();

        for (RequestReceipt r : receipts) {

            Integer designerId = r.getPkg().getDesigner().getId();
            String designerName = r.getPkg().getDesigner().getCustomer().getName();

            if (!designerMap.containsKey(designerName)) {
                Map<String, Object> designerObj = new HashMap<>();
                designerObj.put("designerId", designerId);
                designerObj.put("designerName", designerName);
                designerObj.put("packages", new ArrayList<Map<String, Object>>());
                designerMap.put(designerName, designerObj);
            }

            Map<String, Object> designerObj = designerMap.get(designerName);
            List<Map<String, Object>> packages = (List<Map<String, Object>>) designerObj.get("packages");

            Map<String, Object> pkgObj = new HashMap<>();
            pkgObj.put("id", r.getPkg().getId());
            pkgObj.put("name", r.getPkg().getName());
            pkgObj.put("pkgHeaderContent", r.getPkg().getHeaderContent());
            pkgObj.put("pkgDuration", r.getPkg().getDeliveryDuration());
            pkgObj.put("pkgRevisionTime", r.getPkg().getRevisionTime());
            pkgObj.put("pkgFee", r.getPkg().getFee());


            packages.add(pkgObj);
        }

        List<Map<String, Object>> result = new ArrayList<>(designerMap.values());

        return ResponseBuilder.build(HttpStatus.OK, "list grouped receipt", result);
    }

    @Override
    public ResponseEntity<ResponseObject> addPackageToReceipt(AddPackageToReceiptRequest request) {


        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }

        List<RequestReceipt> requestReceiptList = requestReceiptRepo.findAllByDesignRequest_Id(request.getDesignRequestId());

        List<Integer> addedPackages = new ArrayList<>();
        List<Integer> skippedPackages = new ArrayList<>();

        for (Integer pkgId : request.getPackageId()){
            Packages packages = packagesRepo.findById(pkgId).orElse(null);
            if (packages == null) {
                skippedPackages.add(pkgId);
            }
            boolean alreadyExists = requestReceiptList.stream()
                    .anyMatch(r -> r.getPkg().getId().equals(pkgId));

            if (alreadyExists) {
                skippedPackages.add(pkgId);
            }
            RequestReceipt requestReceipt = RequestReceipt.builder()
                    .pkg(packages)
                    .designRequest(designRequest)
                    .acceptanceDeadline(request.getAcceptanceDeadline())
                    .status(Status.RECEIPT_PENDING)
                    .build();

            requestReceiptRepo.save(requestReceipt);
            addedPackages.add(pkgId);
        }

        return ResponseBuilder.build(HttpStatus.OK, "PackageIds " + addedPackages + " add successfully, packageIds" + skippedPackages + " cannot add", null);
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


    //-----------------------PACKAGE-------------------------//

    @Override
    public ResponseEntity<ResponseObject> getListPackage(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<Packages> packagesList = packagesRepo.findAllByDesigner_Customer_Account_Id(account.getId());

        List<Map<String, Object>> packageMap = packagesList.stream().map(
                packages -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("deliveryDuration", packages.getDeliveryDuration());
                    map.put("designer", packages.getDesigner().getId());
                    map.put("id", packages.getId());
                    map.put("revisionTime", packages.getRevisionTime());
                    map.put("fee", packages.getFee());
                    map.put("headerContent", packages.getHeaderContent());
                    map.put("name", packages.getName());
                    map.put("status", packages.getStatus());
                    return map;
                }
        ).toList();
        return ResponseBuilder.build(HttpStatus.OK, "List of Packages", packageMap);
    }

    @Override
    public ResponseEntity<ResponseObject> createPackages(HttpServletRequest httpRequest, CreatePackagesRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<Packages> designerPackages = packagesRepo
                .findAllByDesigner_Customer_Account_Id(account.getId())
                .stream()
                .filter(pkg -> pkg.getStatus() == Status.PACKAGE_ACTIVE || pkg.getStatus() == Status.PACKAGE_INACTIVE)
                .toList();

        if (designerPackages.size() >= 5) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "You can only create up to 5 active/inactive packages", null);
        }

        Status statusKey = null;

        if (request.getStatus().equalsIgnoreCase("active")) {
            statusKey = Status.PACKAGE_ACTIVE;
        }
        if (request.getStatus().equalsIgnoreCase("inactive")) {
            statusKey = Status.PACKAGE_INACTIVE;
        }

        Packages packages = Packages.builder()
                .deliveryDuration(request.getDeliveryDuration())
                .designer(account.getCustomer().getPartner())
                .revisionTime(request.getRevisionTime())
                .fee(request.getFee())
                .headerContent(request.getHeaderContent())
                .name(request.getName())
                .status(statusKey)
                .build();

        packagesRepo.save(packages);

        return ResponseBuilder.build(HttpStatus.CREATED, "Package created", null);
    }

    @Override
    public ResponseEntity<ResponseObject> changePackageStatus(ChangePackageStatusRequest request) {

        Packages packages = packagesRepo.findById(request.getPackageId()).orElse(null);
        if (packages == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Package not found", null);
        }

        Status statusKey = null;

        if (request.getStatus().equalsIgnoreCase("active")) {
            statusKey = Status.PACKAGE_ACTIVE;
        }
        if (request.getStatus().equalsIgnoreCase("inactive")) {
            statusKey = Status.PACKAGE_INACTIVE;
        }
        if (request.getStatus().equals("delete")) {
            List<DesignRequest> designRequests = designRequestRepo.findAllByPackageId(packages.getId());

            boolean hasUnfinalRequest = designRequests.stream().anyMatch(dr -> {

                List<DesignDelivery> deliveries = designDeliveryRepo.findAllByDesignRequest_Id(dr.getId());

                return deliveries.stream().anyMatch(delivery -> {
                    boolean isFinal = schoolDesignRepo.existsByDesignDelivery_Id(delivery.getId());
                    return !isFinal;
                });
            });

            if (hasUnfinalRequest) {
                statusKey = Status.PACKAGE_PENDING_DELETE;
            } else {
                statusKey = Status.PACKAGE_DELETE;
            }

        }

        assert statusKey != null;
        if (!request.getStatus().equalsIgnoreCase("active")
                && !request.getStatus().equalsIgnoreCase("inactive")
                && !request.getStatus().equalsIgnoreCase("delete")) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Only can change to active or inactive or delete", null);
        }

        packages.setStatus(statusKey);
        packagesRepo.save(packages);
        return ResponseBuilder.build(HttpStatus.OK, "Package changed", null);
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

    private ResponseEntity<ResponseObject> getResponseObjectResponseEntity(List<DesignRequest> designRequests) {
        List<Map<String, Object>> designRequestMaps = designRequests.stream().map(
                designRequest -> {
                    Map<String, Object> designRequestMap = new HashMap<>();
                    designRequestMap.put("id", designRequest.getId());
                    designRequestMap.put("schoolAdmin", designRequest.getSchool().getName());
                    designRequestMap.put("school", designRequest.getSchool().getBusinessName());
                    designRequestMap.put("feedback", null);
                    designRequestMap.put("template", null);
                    designRequestMap.put("pkgId", designRequest.getPackageId());
                    designRequestMap.put("name", designRequest.getName());
                    designRequestMap.put("creationDate", designRequest.getCreationDate());
                    designRequestMap.put("logoImage", designRequest.getLogoImage());
                    designRequestMap.put("privacy", designRequest.isPrivacy());
                    designRequestMap.put("status", designRequest.getStatus().getValue());
                    designRequestMap.put("pkgName", designRequest.getPackageName());
                    designRequestMap.put("pkgHeaderContent", designRequest.getHeaderContent());
                    designRequestMap.put("pkgDeliveryWithin", designRequest.getPackageDeliveryWithin() != null ? designRequest.getPackageDeliveryWithin() : "N/A");
                    designRequestMap.put("pkgRevisionTime", designRequest.getRevisionTime() != null ? designRequest.getRevisionTime() : "N/A");
                    designRequestMap.put("pkgFee", designRequest.getPackagePrice());

                    List<DesignItem> designItems = designRequest.getDesignItems();

                    List<Map<String, Object>> itemMaps = designItems.stream()
                            .map(
                                    designItem -> {
                                        Map<String, Object> itemMap = new HashMap<>();
                                        itemMap.put("id", designItem.getId());
                                        itemMap.put("itemType", designItem.getType());
                                        itemMap.put("itemCategory", designItem.getCategory());
                                        itemMap.put("gender", designItem.getGender());

                                        Map<String, Object> fabricMap = new HashMap<>();
                                        fabricMap.put("fabricId", designItem.getFabric().getId());
                                        fabricMap.put("fabricName", designItem.getFabric().getName());
                                        fabricMap.put("fabricCategory", designItem.getFabric().getDesignItemCategory().toString());
                                        fabricMap.put("fabricType", designItem.getFabric().getDesignItemType().toString());
                                        itemMap.put("fabric", fabricMap);

                                        List<SampleImage> sampleImages = designItem.getSampleImages();

                                        List<Map<String, Object>> imageMap = sampleImages.stream().map(
                                                sampleImage -> {
                                                    Map<String, Object> image = new HashMap<>();
                                                    image.put("id", sampleImage.getId());
                                                    image.put("url", sampleImage.getImageUrl());
                                                    return image;
                                                }
                                        ).toList();
                                        itemMap.put("images", imageMap);
                                        itemMap.put("color", designItem.getColor());
                                        itemMap.put("logoPosition", designItem.getLogoPosition());
                                        itemMap.put("note", designItem.getNote());
                                        return itemMap;
                                    }
                            ).toList();
                    designRequestMap.put("listItemDesign", itemMaps);

                    return designRequestMap;
                }
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", designRequestMaps);
    }

}
