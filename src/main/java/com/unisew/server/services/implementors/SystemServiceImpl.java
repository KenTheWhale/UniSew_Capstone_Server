package com.unisew.server.services.implementors;

import com.unisew.server.enums.DeliveryItemSize;
import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.models.Account;
import com.unisew.server.models.Fabric;
import com.unisew.server.models.PlatformConfig;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.FabricRepo;
import com.unisew.server.repositories.PlatformConfigRepo;
import com.unisew.server.requests.CreateConfigDataRequest;
import com.unisew.server.requests.UpdateGarmentFabricRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import com.unisew.server.services.JWTService;
import com.unisew.server.services.SystemService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
import com.unisew.server.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final PlatformConfigRepo platformConfigRepo;
    private final FabricRepo fabricRepo;
    private final DesignService designService;
    private final JWTService jwtService;
    private final AccountRepo accountRepo;

    @Override
    public ResponseEntity<ResponseObject> getConfigData() {
        List<PlatformConfig> configs = platformConfigRepo.findAll();
        Map<String, Object> data = new HashMap<>();
        for (PlatformConfig config : configs) {
            String key = config.getKey();
            Map<String, Object> value = (Map<String, Object>) config.getValue();
            data.put(key, value);
        }

        data.put("fabrics", (Map<String, Object>) designService.getAllFabric().getBody().getBody());

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    public ResponseEntity<ResponseObject> getConfigDataByKey(String key) {
        Map<String, Object> data = getConfigByKey(key);
        if(data == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Data invalid", null);

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    private Map<String, Object> getConfigByKey(String key){
        if(key.equalsIgnoreCase("fabric")) return (Map<String, Object>) designService.getAllFabric().getBody().getBody();
        PlatformConfig config = platformConfigRepo.findByKey(key).orElse(null);
        if (config == null) return null;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> value = (Map<String, Object>) config.getValue();
        data.put(key, value);

        return data;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateConfigData(CreateConfigDataRequest request) {
        if (request.getFabricDataList() == null || request.getFabricDataList().isEmpty() || request.getBusinessData() == null || request.getMediaData() == null || request.getDesignData() == null || request.getOrderData() == null || request.getReportData() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Data missing", null);
        }

        updateConfig(request);
        updateFabric(request);
        return ResponseBuilder.build(HttpStatus.OK, "Update successfully", null);
    }

    @Transactional
    public void updateConfig(CreateConfigDataRequest request) {
        updateBusiness(request);
        updateMedia(request);
        updateDesign(request);
        updateOrder(request);
        updateReport(request);
    }

    @Transactional
    public void updateBusiness(CreateConfigDataRequest request){
        CreateConfigDataRequest.BusinessData businessData = request.getBusinessData();
        Map<String, Object> businessJson = new HashMap<>();

        businessJson.put("taxRate", businessData.getTaxRate());
        businessJson.put("serviceRate", businessData.getServiceRate());
        businessJson.put("minPay", businessData.getMinPay());
        businessJson.put("maxPay", businessData.getMaxPay());

        PlatformConfig config = platformConfigRepo.findByKey("business").orElse(null);
        assert config != null;
        config.setValue(businessJson);
        platformConfigRepo.save(config);
    }

    @Transactional
    public void updateMedia(CreateConfigDataRequest request){
        CreateConfigDataRequest.MediaData mediaData = request.getMediaData();
        Map<String, Object> mediaJson = new HashMap<>();

        mediaJson.put("maxImgSize", mediaData.getMaxImgSize());
        mediaJson.put("maxVideoSize", mediaData.getMaxVideoSize());
        mediaJson.put("maxDesignRefImg", mediaData.getMaxDesignRefImg());
        mediaJson.put("maxFeedbackImg", mediaData.getMaxFeedbackImg());
        mediaJson.put("maxFeedbackVideo", mediaData.getMaxFeedbackVideo());
        mediaJson.put("maxReportImg", mediaData.getMaxReportImg());
        mediaJson.put("maxReportVideo", mediaData.getMaxReportVideo());
        mediaJson.put("maxGarmentThumbnail", mediaData.getMaxGarmentThumbnail());
        mediaJson.put("maxDesignerThumbnail", mediaData.getMaxDesignerThumbnail());

        List<Map<String, String>> imgFormats = mediaData.getImageFormats().stream()
                .map(format -> {
                    Map<String, String> f = new HashMap<>();
                    f.put("format", "." + format.getFormat());
                    return f;
                })
                .toList();
        mediaJson.put("imgFormat", imgFormats);

        List<Map<String, String>> videoFormats = mediaData.getVideoFormats().stream()
                .map(format -> {
                    Map<String, String> f = new HashMap<>();
                    f.put("format", "." + format.getFormat());
                    return f;
                })
                .toList();
        mediaJson.put("videoFormat", videoFormats);

        PlatformConfig config = platformConfigRepo.findByKey("media").orElse(null);
        assert config != null;
        config.setValue(mediaJson);
        platformConfigRepo.save(config);
    }

    @Transactional
    public void updateDesign(CreateConfigDataRequest request){
        CreateConfigDataRequest.DesignData designData = request.getDesignData();
        Map<String, Object> designJson = new HashMap<>();

        designJson.put("illustrationImage", designData.getIllustrationImage());
        List<Map<String, String>> logoPos = designData.getPositions().stream()
                .map(position -> {
                    Map<String, String> p = new HashMap<>();
                    p.put("p", position.getPosition());
                    return p;
                })
                .toList();
        designJson.put("positions", logoPos);

        PlatformConfig config = platformConfigRepo.findByKey("design").orElse(null);
        assert config != null;
        config.setValue(designJson);
        platformConfigRepo.save(config);
    }

    @Transactional
    public void updateOrder(CreateConfigDataRequest request){
        CreateConfigDataRequest.OrderData orderData = request.getOrderData();
        Map<String, Object> orderJson = new HashMap<>();

        orderJson.put("minUniformQty", orderData.getMinUniformQty());
        orderJson.put("maxAssignedMilestone", orderData.getMaxAssignedMilestone());

        PlatformConfig config = platformConfigRepo.findByKey("order").orElse(null);
        assert config != null;
        config.setValue(orderJson);
        platformConfigRepo.save(config);
    }

    @Transactional
    public void updateReport(CreateConfigDataRequest request){
        CreateConfigDataRequest.ReportData reportData = request.getReportData();
        Map<String, Object> reportJson = new HashMap<>();

        reportJson.put("maxDisbursementDay", reportData.getMaxDisbursementDay());
        List<Map<String, String>> severityLevels = reportData.getLevels().stream()
                .map(level -> {
                    Map<String, String> l = new HashMap<>();
                    l.put("name", level.getName());
                    l.put("compensation", level.getCompensation());
                    return l;
                })
                .toList();
        reportJson.put("severityLevels", severityLevels);

        PlatformConfig config = platformConfigRepo.findByKey("report").orElse(null);
        assert config != null;
        config.setValue(reportJson);
        platformConfigRepo.save(config);
    }

    @Transactional
    public void updateFabric(CreateConfigDataRequest request) {
        List<CreateConfigDataRequest.FabricData> fabrics = request.getFabricDataList();
        try {
            for (CreateConfigDataRequest.FabricData fabric : fabrics) {
                DesignItemType type = DesignItemType.valueOf(fabric.getType().toUpperCase());
                DesignItemCategory category = DesignItemCategory.valueOf(fabric.getCategory().toUpperCase());
                Fabric f = fabricRepo.findByNameAndDesignItemTypeAndDesignItemCategory(fabric.getName(), type, category).orElse(null);
                if (f == null) {
                    fabricRepo.save(Fabric.builder()
                            .name(fabric.getName())
                            .description(fabric.getDescription())
                            .designItemType(type)
                            .designItemCategory(category)
                            .build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getGarmentFabric(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        if(account == null || account.getCustomer() == null || account.getCustomer().getPartner() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account invalid", null);
        }

        List<Fabric> fabrics = fabricRepo.findAll();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> nonPriceFabrics = new ArrayList<>();
        List<Map<String, Object>> hasPriceFabrics = new ArrayList<>();

        for(Fabric fabric : fabrics) {
            Map<String, Object> fabricData = EntityResponseBuilder.buildFabricResponse(fabric);
            Object garmentPrice = fabric.getGarmentPrice();
            if(garmentPrice == null) {
                fabricData.put("price", new ArrayList<>());
                nonPriceFabrics.add(fabricData);
            }else {
                Object priceDataMap;
                if((priceDataMap = getGarmentPrice(garmentPrice, account.getCustomer().getPartner().getId())) == null){
                    fabricData.put("price", new ArrayList<>());
                    nonPriceFabrics.add(fabricData);
                }else {
                    Map<String, Object> priceData = (Map<String, Object>) priceDataMap;
                    List<Map<String, Object>> sizeData = priceData.keySet().stream()
                            .map(key -> {
                                DeliveryItemSize size = DeliveryItemSize.valueOf(key.toUpperCase());
                                Map<String, Object> sizeMap = new HashMap<>();
                                sizeMap.put("enumName", size.name().toUpperCase());
                                sizeMap.put("name", size.getSize().toUpperCase());
                                sizeMap.put("price", ((Integer) priceData.get(key)).longValue());
                                return sizeMap;
                            })
                            .toList();


                    fabricData.put("price", sizeData);
                    hasPriceFabrics.add(fabricData);
                }
            }
        }
        data.put("nonPrice", nonPriceFabrics);
        data.put("hasPrice", hasPriceFabrics);
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    private Object getGarmentPrice(Object fabricGarmentPrice, int garmentId){
        Map<String, Object> data = (Map<String, Object>) fabricGarmentPrice;
        return data.get("garment_" + garmentId);
    }

    @Override
    public ResponseEntity<ResponseObject> getGarmentFabricForQuotation(String orderId) {
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateGarmentFabric(UpdateGarmentFabricRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if(account == null || account.getCustomer() == null || account.getCustomer().getPartner() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account invalid", null);
        }

        int garmentId = account.getCustomer().getPartner().getId();

        boolean process = false;

        for(UpdateGarmentFabricRequest.Fabric garmentFabric: request.getFabrics()){
            Fabric fabric = fabricRepo.findById(garmentFabric.getFabricId()).orElse(null);
            if(fabric == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Fabric invalid", null);

            Object fabricGarmentPrice =  fabric.getGarmentPrice();

            if(fabricGarmentPrice == null) {
                process = createNewGarmentPrice(garmentFabric.getSizePrices(), fabric, garmentId);
            }else {
                process = updateExistedGarmentPrice(garmentFabric.getSizePrices(), fabric, garmentId);
            }
        }

        if(process) return ResponseBuilder.build(HttpStatus.OK, "Price updated", null);

        return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Price not updated", null);
    }

    private boolean createNewGarmentPrice(List<UpdateGarmentFabricRequest.SizePrice> sizePrices, Fabric fabric, int garmentId){
        Map<String, Object> data = new HashMap<>();
        try{
            Map<String, Object> sizeData = new HashMap<>();
            for(UpdateGarmentFabricRequest.SizePrice sizePrice: sizePrices){
                DeliveryItemSize size = DeliveryItemSize.valueOf(sizePrice.getSizeEnumName().toUpperCase());
                if(sizePrice.getPrice() <= 0) return false;
                sizeData.put(size.name(), sizePrice.getPrice());
            }
            data.put("garment_" + garmentId, sizeData);
            fabric.setGarmentPrice(data);
            fabricRepo.save(fabric);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    private boolean updateExistedGarmentPrice(List<UpdateGarmentFabricRequest.SizePrice> sizePrices, Fabric fabric, int garmentId){
        Map<String, Object> data = (Map<String, Object>) fabric.getGarmentPrice();
        if(data.get("garment_" + garmentId) != null) {
            Map<String, Object> sizeData = new HashMap<>();
            for(UpdateGarmentFabricRequest.SizePrice sizePrice: sizePrices){
                sizeData.put(sizePrice.getSizeEnumName().toUpperCase(), sizePrice.getPrice());
            }

            data.replace("garment_" + garmentId, sizeData);

            fabric.setGarmentPrice(data);
            fabricRepo.save(fabric);
        }else {
            Map<String, Object> sizeData = new HashMap<>();
            for(UpdateGarmentFabricRequest.SizePrice sizePrice: sizePrices){
                sizeData.put(sizePrice.getSizeEnumName().toUpperCase(), sizePrice.getPrice());
            }

            data.put("garment_" + garmentId, sizeData);

            fabric.setGarmentPrice(data);
            fabricRepo.save(fabric);
        }

        return true;
    }
}
