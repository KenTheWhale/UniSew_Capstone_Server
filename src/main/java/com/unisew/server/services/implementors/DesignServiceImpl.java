package com.unisew.server.services.implementors;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Gender;
import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.AddPackageToReceiptRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import com.unisew.server.utils.ResponseBuilder;
import com.unisew.server.validations.CreateDesignValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DesignServiceImpl implements DesignService {

    private final DesignRequestRepo designRequestRepo;
    private final FabricRepo fabricRepo;
    private final SampleImageRepo sampleImageRepo;
    private final DesignItemRepo designItemRepo;
    private final PackagesRepo packagesRepo;
    private final RequestReceiptRepo requestReceiptRepo;


    //-----------------------------------DESIGN_REQUEST---------------------------------------//

    @Override
    public ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest) {

        String errorMessage = CreateDesignValidation.validate(createDesignRequest);

        if (!errorMessage.isEmpty()) {
            ResponseBuilder.build(HttpStatus.BAD_REQUEST, errorMessage, null);
        }

        DesignRequest designRequest = DesignRequest.builder()
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


        return ResponseBuilder.build(HttpStatus.CREATED, "create design request successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> viewListDesignRequests() {

        List<DesignRequest> designRequests = designRequestRepo.findAll();

        return getResponseObjectResponseEntity(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> getListDesignRequestByCustomerId(int customerId) {

        List<DesignRequest> designRequests = designRequestRepo.findAllBySchool_Id(customerId);

        return getResponseObjectResponseEntity(designRequests);
    }

    @Override
    public ResponseEntity<ResponseObject> pickPackage(int packageId, int designRequestId) {

        Packages packages = packagesRepo.findById(packageId).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(designRequestId).orElse(null);

        if (packages == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "package not found", null);
        }
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }
        if (!designRequest.getStatus().equals(Status.DESIGN_REQUEST_CREATED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "package already exists for this request", null);
        }

        designRequest.setPackageId(packageId);
        designRequest.setPackagePrice(packages.getFee());
        designRequest.setPackageName(packages.getName());
        designRequest.setHeaderContent(packages.getHeaderContent());
        designRequest.setPackageDeliveryWithin(packages.getDeliveryDuration());
        designRequest.setRevisionTime(packages.getRevisionTime());
        designRequest.setStatus(Status.DESIGN_REQUEST_PENDING);
        designRequestRepo.save(designRequest);

        return ResponseBuilder.build(HttpStatus.OK, "pick package successfully", null);
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
    public ResponseEntity<ResponseObject> getListReceipt(int designRequestId) {
        List<RequestReceipt> receipts = requestReceiptRepo.findAllByDesignRequest_Id(designRequestId);

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
            pkgObj.put("pkgHeaderContent",r.getPkg().getHeaderContent());
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

        Packages packages = packagesRepo.findById(request.getPackageId()).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (packages == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "package not found", null);
        }
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "request not found", null);
        }

        List<RequestReceipt> requestReceiptList = requestReceiptRepo.findAllByDesignRequest_Id(request.getDesignRequestId());

        boolean alreadyExists = requestReceiptList.stream()
                .anyMatch(r -> r.getPkg().getId().equals(request.getPackageId()));

        if (alreadyExists) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "package already added to receipt for this request", null);
        }

        RequestReceipt requestReceipt = RequestReceipt.builder()
                .pkg(packages)
                .designRequest(designRequest)
                .acceptanceDeadline(request.getAcceptanceDeadline())
                .status(Status.RECEIPT_PENDING)
                .build();
        requestReceiptRepo.save(requestReceipt);

        return ResponseBuilder.build(HttpStatus.OK, "package added to receipt successfully", null);
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
                    designRequestMap.put("name", designRequest.getName());
                    designRequestMap.put("creationDate", designRequest.getCreationDate());
                    designRequestMap.put("status",designRequest.getStatus().getValue());
                    designRequestMap.put("pkgId", designRequest.getPackageId() != null ? designRequest.getPackageId() : "N/A");
                    designRequestMap.put("pkgName", designRequest.getPackageName() != null ? designRequest.getPackageName() : "N/A");
                    designRequestMap.put("pkgHeaderContent", designRequest.getHeaderContent() != null ? designRequest.getHeaderContent() : "N/A");
                    designRequestMap.put("pkgDuration", designRequest.getPackageDeliveryWithin() != null ? designRequest.getPackageDeliveryWithin() : "N/A");
                    designRequestMap.put("pkgRevisionTime", designRequest.getRevisionTime() != null ? designRequest.getRevisionTime() : "N/A");
                    designRequestMap.put("pkgFee", designRequest.getPackagePrice());
                    designRequestMap.put("numberOfItem", designRequest.getDesignItems().size());

                    List<DesignItem> designItems = designRequest.getDesignItems();

                    List<Map<String,Object>> itemMaps = designItems.stream()
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

        return ResponseBuilder.build(HttpStatus.OK,"list design requests successfully",designRequestMaps);
    }



}
