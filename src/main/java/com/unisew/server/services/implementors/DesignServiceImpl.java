package com.unisew.server.services.implementors;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Gender;
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
import com.unisew.server.models.Partner;
import com.unisew.server.models.RevisionRequest;
import com.unisew.server.models.SampleImage;
import com.unisew.server.models.SchoolDesign;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.DeliveryItemRepo;
import com.unisew.server.repositories.DesignDeliveryRepo;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.DesignQuotationRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.FabricRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.RevisionRequestRepo;
import com.unisew.server.repositories.SampleImageRepo;
import com.unisew.server.repositories.SchoolDesignRepo;
import com.unisew.server.requests.CancelRequest;
import com.unisew.server.requests.CreateDesignQuotationRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.requests.CreateNewDeliveryRequest;
import com.unisew.server.requests.CreateRevisionRequest;
import com.unisew.server.requests.DuplicateRequest;
import com.unisew.server.requests.GetListDeliveryRequest;
import com.unisew.server.requests.GetUnUseListRevisionRequest;
import com.unisew.server.requests.ImportDesignRequest;
import com.unisew.server.requests.MakeDesignFinalRequest;
import com.unisew.server.requests.PickDesignQuotationRequest;
import com.unisew.server.requests.UpdateRequestByDeadline;
import com.unisew.server.requests.UpdateRevisionTimeRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.PaymentService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
import com.unisew.server.utils.ResponseBuilder;
import com.unisew.server.validations.BuyRevisionValidation;
import com.unisew.server.validations.CancelRequestValidation;
import com.unisew.server.validations.CreateDesignValidation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final SchoolDesignRepo schoolDesignRepo;
    private final PaymentService paymentService;
    private final PartnerRepo partnerRepo;


    //-----------------------------------DESIGN_REQUEST---------------------------------------//
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest, HttpServletRequest httpRequest) {

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        String errorMessage = CreateDesignValidation.validate(createDesignRequest, designRequestRepo, account.getCustomer());

        if (!errorMessage.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, errorMessage, null);
        }

        if (account.getStatus().equals(Status.ACCOUNT_INACTIVE)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account is inactive", null);
        }

        DesignRequest designRequest = DesignRequest.builder()
                .school(account.getCustomer())
                .creationDate(LocalDateTime.now())
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

        return buildDesignRequestResponseForSchool(designRequests, designQuotationRepo, designRequestRepo);
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
    public ResponseEntity<ResponseObject> getListRejectedDesignRequestByDesigner(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        List<DesignQuotation> quotations = account.getCustomer().getPartner().getDesignQuotations().stream().filter(quotation -> quotation.getStatus().equals(Status.DESIGN_QUOTATION_REJECTED)).toList();

        List<DesignRequest> designRequests = new ArrayList<>();

        for (DesignQuotation quotation : quotations) {
           designRequests.add(quotation.getDesignRequest());
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
            designRequest.setCreationDate(LocalDateTime.now());
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
                .creationDate(LocalDateTime.now())
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
    public ResponseEntity<ResponseObject> buyRevisionTime(UpdateRevisionTimeRequest request, HttpServletRequest httpRequest) {

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design request not found", null);
        }

        DesignQuotation designQuotation = designQuotationRepo.findById(designRequest.getDesignQuotationId()).orElse(null);
        if (designQuotation == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Design quotation not found", null);
        }

        String message = BuyRevisionValidation.validate(designRequest, request, designQuotation);

        if (message != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, message, null);
        }

        designRequest.setRevisionTime(request.getRevisionTime());
        designRequestRepo.save(designRequest);

        return paymentService.createTransaction(request.getCreateTransactionRequest(), httpRequest);
    }

    @Override
    public ResponseEntity<ResponseObject> cancelRequest(CancelRequest request, HttpServletRequest httpServletRequest) {

        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);

        DesignRequest designRequest = designRequestRepo.findById(request.getRequestId()).orElse(null);

        ResponseEntity<ResponseObject> validationResponse =
                CancelRequestValidation.validate(account, designRequest);

        if (validationResponse != null) {
            return validationResponse;
        }

        designRequest.setStatus(Status.DESIGN_REQUEST_CANCELED);
        designRequest.setCancelReason(request.getReason());
        designRequestRepo.save(designRequest);

        return ResponseBuilder.build(HttpStatus.OK, "Design request cancelled", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> importDesign(ImportDesignRequest request, HttpServletRequest httpRequest) {
        String error = validateImportDesign(request);
        if(!error.isEmpty()){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        try {
            Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
            if(account == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid user", null);
            Customer school = account.getCustomer();
            LocalDateTime today = LocalDateTime.now();

            DesignRequest designRequest = designRequestRepo.save(
                    DesignRequest.builder()
                            .school(school)
                            .feedback(null)
                            .designQuotationId(null)
                            .name(request.getDesignData().getName())
                            .creationDate(today)
                            .logoImage(request.getDesignData().getLogoImage())
                            .price(0)
                            .privacy(true)
                            .status(Status.DESIGN_REQUEST_IMPORTED)
                            .revisionTime(0)
                            .build()
            );


            DesignDelivery designDelivery = designDeliveryRepo.save(
                    DesignDelivery.builder()
                            .designRequest(designRequest)
                            .revisionRequest(null)
                            .name(request.getDesignData().getName())
                            .version(0)
                            .submitDate(today)
                            .revision(false)
                            .note("")
                            .build()
            );
            for (ImportDesignRequest.DesignItemData designItemData: request.getDesignItemDataList()){
                DesignItemType type = DesignItemType.valueOf(designItemData.getType().toUpperCase());
                DesignItemCategory category = DesignItemCategory.valueOf(designItemData.getCategory().toUpperCase());
                Gender gender = Gender.valueOf(designItemData.getGender().toUpperCase());
                Fabric fabric = fabricRepo.findById(designItemData.getFabricId()).orElse(null);
                assert fabric != null;

                DesignItem item = designItemRepo.save(
                        DesignItem.builder()
                                .designRequest(designRequest)
                                .fabric(fabric)
                                .type(type)
                                .category(category)
                                .logoPosition(designItemData.getLogoPosition())
                                .color(designItemData.getColor())
                                .note("")
                                .gender(gender)
                                .build()
                );

                Map<String, Object> accessory = new HashMap<>();
                accessory.put("button", null);
                accessory.put("logo", null);
                accessory.put("zipper", false);
                if(item.getType().equals(DesignItemType.SHIRT)){
                    Map<String, Object> buttonData = new HashMap<>();
                    Map<String, Object> logoData = new HashMap<>();

                    buttonData.put("quantity", designItemData.getButtonData().getQuantity());
                    buttonData.put("height", designItemData.getButtonData().getHeight());
                    buttonData.put("width", designItemData.getButtonData().getWidth());
                    buttonData.put("holeQty", designItemData.getButtonData().getHoleQty());
                    buttonData.put("color", designItemData.getButtonData().getColor());
                    buttonData.put("note", designItemData.getButtonData().getNote());

                    logoData.put("attachingTechnique", designItemData.getLogoData().getAttachingTechnique());
                    logoData.put("baseHeight", designItemData.getLogoData().getBaseHeight());
                    logoData.put("baseWidth", designItemData.getLogoData().getBaseWidth());
                    logoData.put("note", designItemData.getLogoData().getNote());

                    accessory.replace("button", buttonData);
                    accessory.replace("logo", logoData);
                }

                if(item.getType().equals(DesignItemType.PANTS)){
                    accessory.replace("zipper", designItemData.isZipper());
                }

                deliveryItemRepo.save(
                        DeliveryItem.builder()
                                .designDelivery(designDelivery)
                                .designItemId(item.getId())
                                .frontImageUrl(designItemData.getFrontImage())
                                .backImageUrl(designItemData.getBackImage())
                                .accessory(accessory)
                                .build()
                );

            }
            schoolDesignRepo.save(
                    SchoolDesign.builder()
                            .designDelivery(designDelivery)
                            .customer(school)
                            .build()
            );

            return ResponseBuilder.build(HttpStatus.CREATED, "Import successfully", null);
        }catch (Exception e){
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e);
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Something wrong", errorData);
        }
    }

    private String validateImportDesign(ImportDesignRequest request){
        if(request.getDesignData() == null || request.getDesignItemDataList() == null ||request.getDesignItemDataList().isEmpty()){
            return "Missing data";
        }

        ImportDesignRequest.DesignData designData = request.getDesignData();
        List<ImportDesignRequest.DesignItemData> designItemData = request.getDesignItemDataList();

        if(validateStringData(designData.getName())) return "Name invalid";
        if(validateStringData(designData.getLogoImage())) return "Logo invalid";
        for (ImportDesignRequest.DesignItemData data: designItemData){
            if(validateStringData(data.getType())) return "Type invalid at index " + designItemData.indexOf(data);
            if(validateStringData(data.getCategory())) return "Category invalid at index " + designItemData.indexOf(data);
            if(validateStringData(data.getLogoPosition()) && data.getType().equalsIgnoreCase(DesignItemType.SHIRT.getValue())) return "Logo position invalid at index " + designItemData.indexOf(data);
            if(validateStringData(data.getColor())) return "Color invalid at index " + designItemData.indexOf(data);
            if(validateStringData(data.getGender())) return "Gender invalid at index " + designItemData.indexOf(data);
            if(validateStringData(data.getFrontImage())) return "Front image invalid at index " + designItemData.indexOf(data);
            if(validateStringData(data.getBackImage())) return "Back image invalid at index " + designItemData.indexOf(data);
            if(data.getFabricId() < 0) return "Fabric invalid at index " + designItemData.indexOf(data);
            if(data.getLogoData().getBaseHeight() < 0) return "Logo height invalid at index " + designItemData.indexOf(data);
            if(data.getLogoData().getBaseWidth() < 0) return "Logo width invalid at index " + designItemData.indexOf(data);

        }

        return "";
    }

    private boolean validateStringData(String data){
        return data == null || data.isEmpty();
    }

    //-----------------------------------FABRIC---------------------------------------//
    @Override
    public ResponseEntity<ResponseObject> getAllFabric() {

        List<Fabric> fabrics = fabricRepo.findAll();

        Map<String, Object> categoryMap = new HashMap<>();

        List<Fabric> regularFabrics = fabrics.stream().filter(Fabric::isForRegular).toList();
        Map<String, Object> regularMap = getCateMap(regularFabrics);
        categoryMap.put("regular", regularMap);

        List<Fabric> peFabrics = fabrics.stream().filter(Fabric::isForPE).toList();
        Map<String, Object> peMap = getCateMap(peFabrics);
        categoryMap.put("physical", peMap);

        return ResponseBuilder.build(HttpStatus.OK, "list fabrics", categoryMap);
    }

    private Map<String, Object> getCateMap(List<Fabric> fabrics){
        Map<String, Object> fabricMap = new HashMap<>();
        fabricMap.put("shirts", fabrics.stream().filter(Fabric::isForShirt).map(EntityResponseBuilder::buildFabricResponse).toList());
        fabricMap.put("pants", fabrics.stream().filter(Fabric::isForPants).map(EntityResponseBuilder::buildFabricResponse).toList());
        fabricMap.put("skirts", fabrics.stream().filter(Fabric::isForSkirt).map(EntityResponseBuilder::buildFabricResponse).toList());
        return fabricMap;
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

        List<DesignDelivery> deliveries = designDeliveryRepo.findAllByDesignRequest_Id(request.getDesignRequestId());

        boolean existName = deliveries.stream().anyMatch(designDelivery -> designDelivery.getName().equals(request.getName()));

        if (existName) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "This delivery name already exist", null);
        }
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
                        .submitDate(LocalDateTime.now())
                        .revision(request.isRevision())
                        .note(request.getNote())
                        .version(version)
                        .build()
        );

        for (CreateNewDeliveryRequest.DeliveryItems i : request.getItemList()) {
            Map<String, Object> accessory = new HashMap<>();
            accessory.put("button", "");
            accessory.put("logo", "");
            accessory.put("zipper", false);
            DesignItem item = designItemRepo.findById(i.getDesignItemId()).orElse(null);
            assert item != null;
            if(item.getType().equals(DesignItemType.SHIRT)){
                Map<String, Object> buttonData = new HashMap<>();
                Map<String, Object> logoData = new HashMap<>();

                buttonData.put("quantity", i.getButtonData().getQuantity());
                buttonData.put("height", i.getButtonData().getHeight());
                buttonData.put("width", i.getButtonData().getWidth());
                buttonData.put("holeQty", i.getButtonData().getHoleQty());
                buttonData.put("color", i.getButtonData().getColor());
                buttonData.put("note", i.getButtonData().getNote());

                logoData.put("attachingTechnique", i.getLogoData().getAttachingTechnique());
                logoData.put("baseHeight", i.getLogoData().getBaseHeight());
                logoData.put("baseWidth", i.getLogoData().getBaseWidth());
                logoData.put("note", i.getLogoData().getNote());

                accessory.replace("button", buttonData);
                accessory.replace("logo", logoData);
            }

            if(item.getType().equals(DesignItemType.PANTS)){
                accessory.replace("zipper", i.isZipper());
            }

            deliveryItemRepo.save(
                    DeliveryItem.builder()
                            .designDelivery(delivery)
                            .designItemId(i.getDesignItemId())
                            .backImageUrl(i.getBackUrl())
                            .frontImageUrl(i.getFrontUrl())
                            .accessory(accessory)
                            .build()
            );
        }

        return ResponseBuilder.build(HttpStatus.CREATED, "Upload delivery successfully", null);
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
                .requestDate(LocalDateTime.now())
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
        designDelivery.getDesignRequest().setDisburseAt(Instant.now().plus(7, ChronoUnit.DAYS));
        designDeliveryRepo.save(designDelivery);

        return ResponseBuilder.build(HttpStatus.CREATED, "Design finished", null);
    }

    //-----------------------DESIGN_QUOTATION-------------------------//
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> pickDesignQuotation(PickDesignQuotationRequest request, HttpServletRequest httpRequest) {

        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        if (account.getStatus().equals(Status.ACCOUNT_INACTIVE)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account is inactive", null);
        }

        DesignQuotation designQuotation = designQuotationRepo.findById(request.getDesignQuotationId()).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designQuotation == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Quotation not found", null);
        }
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Request not found", null);
        }
        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_PENDING)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Quotation already exists for this request", null);
        }
        if (LocalDate.now().isAfter(designQuotation.getAcceptanceDeadline())) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Acceptance deadline has passed", null);
        }

        designRequest.setDesignQuotationId(request.getDesignQuotationId());
        designRequest.setRevisionTime(designQuotation.getRevisionTime() + request.getExtraRevision());
        designRequest.setStatus(Status.DESIGN_REQUEST_PROCESSING);
        designRequest.setPrice(designQuotation.getPrice() + request.getCreateTransactionRequest().getServiceFee() + designQuotation.getExtraRevisionPrice() * request.getExtraRevision());
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
        if (designer == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Designer not found", null);

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
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", quotation.getId());
                    data.put("designRequest", EntityResponseBuilder.buildDesignRequestResponse(quotation.getDesignRequest()));
                    data.put("note", quotation.getNote());
                    data.put("deliveryWithIn", quotation.getDeliveryWithIn());
                    data.put("revisionTime", quotation.getRevisionTime());
                    data.put("extraRevisionPrice", quotation.getExtraRevisionPrice());
                    data.put("price", quotation.getPrice());
                    data.put("acceptanceDeadline", quotation.getAcceptanceDeadline());
                    data.put("status", quotation.getStatus().getValue());
                    return data;
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

        if (account.getStatus().equals(Status.ACCOUNT_INACTIVE)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account is inactive", null);
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

    @Override
    public ResponseEntity<ResponseObject> getAllDesignRequest() {
        List<DesignRequest> designRequests = designRequestRepo.findAll().stream()
                .filter(req -> req.getStatus() != Status.DESIGN_REQUEST_IMPORTED)
                .toList();
        return ResponseBuilder.build(HttpStatus.OK, "", EntityResponseBuilder.buildDesignRequestListForAdminResponse(designRequests, designQuotationRepo, designRequestRepo));
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
        Map<String, Object> data = EntityResponseBuilder.buildDesignRequestResponse(designRequest);
        data.put("resultDelivery", buildResultDeliveryResponse(designRequest));
        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", data);
    }

    private ResponseEntity<ResponseObject> buildDesignRequestResponseForSchool(List<DesignRequest> designRequests, DesignQuotationRepo designQuotationRepo, DesignRequestRepo designRequestRepo) {
        List<Map<String, Object>> designRequestMaps = designRequests.stream()
                .map(designRequest -> {
                    Map<String, Object> data = new HashMap<>();

                    // Common fields
                    data.put("id", designRequest.getId());

                    Feedback feedback = designRequest.getFeedback();
                    data.put("feedback", EntityResponseBuilder.buildFeedbackResponse(feedback));

                    data.put("name", designRequest.getName());
                    data.put("creationDate", designRequest.getCreationDate());
                    data.put("logoImage", designRequest.getLogoImage());
                    data.put("privacy", designRequest.isPrivacy());
                    data.put("status", designRequest.getStatus().getValue());
                    data.put("items", EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()));
                    data.put("revisionTime", designRequest.getRevisionTime());
                    data.put("price", designRequest.getPrice());
                    data.put("cancelReason", designRequest.getCancelReason());

                    // Conditional fields based on DesignQuotation
                    DesignQuotation finalQuotation = null;
                    if (designRequest.getDesignQuotationId() != null) {
                        finalQuotation = designQuotationRepo.findById(designRequest.getDesignQuotationId()).orElse(null);
                    }

                    if (finalQuotation != null) {
                        data.put("finalDesignQuotation", EntityResponseBuilder.buildDesignQuotationResponse(finalQuotation, designQuotationRepo, designRequestRepo));
                    } else {
                        data.put("finalDesignQuotation", "");
                    }

                    data.put("designQuotations", EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations(), designQuotationRepo, designRequestRepo));

                    // Conditional field for completed status
                    boolean completed = designRequest.getStatus().equals(Status.DESIGN_REQUEST_COMPLETED);
                    boolean imported = designRequest.getStatus().equals(Status.DESIGN_REQUEST_IMPORTED);
                    if (completed || imported) {
                        data.put("resultDelivery", buildResultDeliveryResponse(designRequest));
                    }

                    return data;
                })
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "list design requests successfully", designRequestMaps);
    }

    private ResponseEntity<ResponseObject> buildDesignRequestResponseForSchool(DesignRequest designRequest) {
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Design request not found", null);
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", designRequest.getId());

        Feedback feedback = designRequest.getFeedback();
        data.put("feedback", Objects.requireNonNullElse(EntityResponseBuilder.buildFeedbackResponse(feedback), ""));

        // Tối ưu hóa việc tìm quotation và bỏ lặp code
        DesignQuotation finalQuotation = null;
        if (designRequest.getDesignQuotationId() != null) {
            finalQuotation = designQuotationRepo.findById(designRequest.getDesignQuotationId()).orElse(null);
        }

        data.put("finalDesignQuotation", Objects.requireNonNullElse(EntityResponseBuilder.buildDesignQuotationResponse(finalQuotation, designQuotationRepo, designRequestRepo), ""));
        data.put("designQuotations", EntityResponseBuilder.buildDesignQuotationListResponse(designRequest.getDesignQuotations(), designQuotationRepo, designRequestRepo));

        data.put("name", designRequest.getName());
        data.put("creationDate", designRequest.getCreationDate());
        data.put("logoImage", designRequest.getLogoImage());
        data.put("privacy", designRequest.isPrivacy());
        data.put("status", designRequest.getStatus().getValue());
        data.put("items", EntityResponseBuilder.buildDesignItemListResponse(designRequest.getDesignItems()));
        data.put("revisionTime", designRequest.getRevisionTime());
        data.put("price", designRequest.getPrice());
        data.put("cancelReason", designRequest.getCancelReason());

        return ResponseBuilder.build(HttpStatus.OK, "Design request retrieved successfully", data);
    }

    //-------Result Delivery---------
    private Map<String, Object> buildResultDeliveryResponse(DesignRequest request) {
        if (request == null || (!request.getStatus().equals(Status.DESIGN_REQUEST_COMPLETED) && !request.getStatus().equals(Status.DESIGN_REQUEST_IMPORTED))) {
            return null;
        }

        SchoolDesign design = schoolDesignRepo.findByDesignDelivery_DesignRequest_Id(request.getId()).orElse(null);
        if (design == null) {
            return null;
        }

        DesignDelivery delivery = design.getDesignDelivery();
        if (delivery == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", delivery.getId());
        data.put("name", delivery.getName());
        data.put("submitDate", delivery.getSubmitDate());
        data.put("version", delivery.getVersion());
        data.put("note", delivery.getNote() == null ? "" : delivery.getNote());
        data.put("items", EntityResponseBuilder.buildDeliveryItemListResponse(delivery.getDeliveryItems(), designItemRepo));

        return data;
    }

}