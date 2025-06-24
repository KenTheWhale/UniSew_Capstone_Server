package com.unisew.server.services.implementors;

import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.models.Packages;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.CreatePackageRequest;
import com.unisew.server.requests.CreateProfileRequest;
import com.unisew.server.requests.CreateServiceRequest;
import com.unisew.server.requests.UpdatePackageRequest;
import com.unisew.server.requests.UpdateServiceRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImpl implements ProfileService {

    DesignerRepo designerRepo;

    ProfileRepo profileRepo;

    PackageRepo packageRepo;

    ServiceRepo serviceRepo;

    PackageServiceRepo packageServiceRepo;

    PartnerRepo partnerRepo;

    // --------------------------------------------Designer Profile--------------------------------------------
    @Override
    public ResponseEntity<ResponseObject> getAllDesignerProfile() {
        List<Designer> designers = designerRepo.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get designer profiles successfully")
                        .data(buildDesigners(designers))
                        .build()
        );
    }


    private List<Map<String, Object>> buildDesigners(List<Designer> designers) {
        return designers.stream()
                .map(designer -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", designer.getId());
                    map.put("short_review", designer.getShortPreview());
                    map.put("bio", designer.getBio());
                    map.put("profile", buildProfile(designer.getProfile()));
                    map.put("package", buildPackage(designer.getPackages()));
                    return map;
                })
                .toList();
    }

    private Map<String, Object> buildProfile(Profile profile) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", profile.getId());
        map.put("name", profile.getName());
        map.put("phone", profile.getPhone());
        map.put("avatar", profile.getAvatar());
        return map;
    }

    private List<Map<String, Object>> buildPackage(List<Packages> pkgs) {
        return pkgs.stream()
                .map(pkg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", pkg.getId());
                    map.put("name", pkg.getName());
                    map.put("header_content", pkg.getHeaderContent());
                    map.put("delivery_duration", pkg.getDeliveryDuration());
                    map.put("revision_time", pkg.getRevisionTime());
                    map.put("fee", pkg.getFee());
                    map.put("status", pkg.getStatus());
                    map.put("services", buildService(pkg.getId()));
                    return map;
                })
                .toList();
    }

    private List<Map<String, Object>> buildService(int pkgId) {
        List<Services> services = serviceRepo.findAllByPackageServices_Pkg_Id(pkgId);
        return services.stream()
                .map(sv -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", sv.getId());
                    map.put("rule", sv.getRule());
                    map.put("creation_date", sv.getCreationDate());
                    map.put("status", sv.getStatus());
                    return map;
                })
                .toList();
    }

    //--------------------------------------------Garment Profile--------------------------------------------

    @Override
    public ResponseEntity<ResponseObject> getAllGarmentProfile() {
        List<Partner> garments = partnerRepo.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get garment profiles successfully")
                        .data(buildGarments(garments))
                        .build()
        );
    }

    private List<Map<String, Object>> buildGarments(List<Partner> partners) {
        return partners.stream()
                .map(partner -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", partner.getId());
                    map.put("street", partner.getStreet());
                    map.put("ward", partner.getWard());
                    map.put("district", partner.getDistrict());
                    map.put("province", partner.getProvince());
                    map.put("isBusy", partner.isBusy());
                    map.put("profile", buildProfile(partner.getProfile()));
                    return map;
                })
                .toList();
    }

    @Override
    public ResponseEntity<ResponseObject> getAllService() {
        List<Services> services = serviceRepo.findAll();
        List<Map<String, Object>> result = services.stream()
                .map(service -> {
                    Map<String, Object> serviceData = new HashMap<>();
                    serviceData.put("id", service.getId());
                    serviceData.put("rule", service.getRule());
                    serviceData.put("creationDate", service.getCreationDate());
                    serviceData.put("status", service.getStatus());
                    return serviceData;
                })
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Get all services successfully")
                        .data(result)
                        .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createService(CreateServiceRequest request) {
        Services service = Services.builder()
                .rule(request.getRule())
                .creationDate(LocalDate.now())
                .status(Status.SERVICE_ACTIVE)
                .build();

        serviceRepo.save(service);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .message("Create service successfully")
                        .build());
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateService(UpdateServiceRequest request) {
        Services service = serviceRepo.findById(request.getId()).orElse(null);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .message("Service not found")
                            .data(null)
                            .build());
        }
        service.setRule(request.getRule());
        service.setStatus(Status.valueOf(request.getStatus()));
        serviceRepo.save(service);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Update service successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ResponseObject> getPackageInfo(int id) {
        Packages pkg = packageRepo.findById(id).orElse(null);
        if (pkg == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get package info successfully")
                        .data(buildPackage(pkg))
                        .build()
        );
    }


    private Map<String, Object> buildPackage(Packages pkg) {
        Map<String, Object> data = new HashMap<>();
        data.put("pkgName", pkg.getName());
        data.put("headerContent", pkg.getHeaderContent());
        data.put("deliveryDuration", pkg.getDeliveryDuration());
        data.put("revisionTime", pkg.getRevisionTime());
        data.put("fee", pkg.getFee());
        data.put("status", pkg.getStatus());
        data.put("designerInfo", buildDesigner(pkg.getDesigner()));
        data.put("services", buildServices(pkg.getPackageServices()));
        return data;
    }

    private Map<String, Object> buildDesigner(Designer designer) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", designer.getProfile().getName());
        data.put("phone", designer.getProfile().getPhone());
        data.put("avatar", designer.getProfile().getAvatar());
        data.put("shortPreview", designer.getShortPreview());
        data.put("thumbnail", designer.getThumbnailImg());
        data.put("bio", designer.getBio());
        return data;
    }

    private List<Map<String, Object>> buildServices(List<PackageService> pkgServices) {
        return pkgServices.stream()
                .map(packageService -> {
                    Services service = packageService.getService();
                    Map<String, Object> serviceData = new HashMap<>();
                    serviceData.put("id", service.getId());
                    serviceData.put("rule", service.getRule());
                    serviceData.put("creationDate", service.getCreationDate());
                    serviceData.put("status", service.getStatus());
                    return serviceData;
                })
                .toList();
    }

    //----------------------------------------Create Package----------------------------------------

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createPackage(CreatePackageRequest request) {
        Designer designer = designerRepo.findById(request.getDesignerId())
                .orElseThrow(() -> new RuntimeException("Designer not found"));

        Packages pkg = Packages.builder()
                .name(request.getName())
                .headerContent(request.getHeaderContent())
                .deliveryDuration(request.getDeliveryDuration())
                .revisionTime(request.getRevisionTime())
                .fee(request.getFee())
                .status(Status.PACKAGE_ACTIVE)
                .designer(designer)
                .build();

        Packages savedPkg = packageRepo.save(pkg);

        List<PackageService> packageServices = request.getServiceIds().stream()
                .map(serviceId -> {
                    Services service = serviceRepo.findById(serviceId).orElse(null);
                    return PackageService.builder()
                            .id(new PackageService.ID(savedPkg.getId(), serviceId))
                            .pkg(savedPkg)
                            .service(service)
                            .build();
                })
                .toList();

        packageServiceRepo.saveAll(packageServices);
        pkg.setPackageServices(packageServices);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .message("Create package successfully")
                        .build());
    }


    //----------------------------------------Update Package----------------------------------------

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updatePackage(UpdatePackageRequest request) {
        Packages pkg = packageRepo.findById(request.getId()).orElse(null);
        if (pkg == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .message("Package not found")
                            .build());
        }
        pkg.setName(request.getName());
        pkg.setHeaderContent(request.getHeaderContent());
        pkg.setDeliveryDuration(request.getDeliveryDuration());
        pkg.setRevisionTime(request.getRevisionTime());
        pkg.setFee(request.getFee());

        if (request.getDesignerId() != null && !request.getDesignerId().equals(pkg.getDesigner().getId())) {
            Designer designer = designerRepo.findById(request.getDesignerId()).orElse(null);
            pkg.setDesigner(designer);
        }

        packageServiceRepo.deleteAll(pkg.getPackageServices());

        List<PackageService> newPackageServices = request.getServiceIds().stream()
                .map(serviceId -> {
                    Services service = serviceRepo.findById(serviceId).orElse(null);
                    return PackageService.builder()
                            .id(new PackageService.ID(pkg.getId(), serviceId))
                            .pkg(pkg)
                            .service(service)
                            .build();
                })
                .toList();

        packageServiceRepo.saveAll(newPackageServices);
        pkg.setPackageServices(new ArrayList<>(newPackageServices));
        packageRepo.save(pkg);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Update package successfully")
                        .build());
    }

    // ----------------------------------------Disable Package----------------------------------------
    @Override
    public ResponseEntity<ResponseObject> disablePackage(int id) {
        Packages pkg = packageRepo.findById(id).orElse(null);
        if (pkg == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("Package not found")
                            .build()
            );
        }
        pkg.setStatus(Status.PACKAGE_UN_ACTIVE);
        packageRepo.save(pkg);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Package disabled successfully")
                        .build());
    }

}
