package com.unisew.server.services.implementors;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Gender;
import com.unisew.server.enums.Status;
import com.unisew.server.models.DesignItem;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.Fabric;
import com.unisew.server.models.SampleImage;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.FabricRepo;
import com.unisew.server.repositories.SampleImageRepo;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import com.unisew.server.utils.ResponseBuilder;
import com.unisew.server.validations.CreateDesignValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DesignServiceImpl implements DesignService {

    private final DesignRequestRepo designRequestRepo;
    private final FabricRepo fabricRepo;
    private final SampleImageRepo sampleImageRepo;
    private final DesignItemRepo designItemRepo;


    //-----------------------------------DESIGN_REQUEST---------------------------------------//

    @Override
    @Transactional
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


        return ResponseBuilder.build(HttpStatus.CREATED, "Create successfully", null);
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

    private ResponseEntity<ResponseObject> getResponseObjectResponseEntity(List<DesignRequest> designRequests) {
        List<Map<String, Object>> designRequestMaps = designRequests.stream().map(
                designRequest -> {
                    Map<String, Object> designRequestMap = new HashMap<>();
                    designRequestMap.put("id", designRequest.getId());
                    designRequestMap.put("name", designRequest.getName());
                    designRequestMap.put("creationDate", designRequest.getCreationDate());
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


}
