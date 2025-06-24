package com.unisew.server.services.implementors;

import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.*;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignRequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequestServiceImpl implements DesignRequestService {

    final DesignRequestRepo designRequestRepo;
    final ClothRepo clothRepo;
    final SampleImageRepo sampleImageRepo;
    final DesignDraftRepo designDraftRepo;
    final RevisionRequestRepo revisionRequestRepo;
    final DesignCommentRepo designCommentRepo;
    final AccountRepo accountRepo;
    final PackageRepo packageRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllDesignRequests() {
        // Change id to name after have another service
        List<Map<String, Object>> designRequests = designRequestRepo.findAll().stream().map(
                request -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", request.getId());
                    response.put("school", request.getSchool().getId());
                    response.put("private", request.isPrivate());
                    response.put("package", request.getPkg() != null ?  request.getPkg().getId() : null);
                    response.put("feedback", request.getFeedback() != null ? request.getFeedback().getId() : null);
                    response.put("status", request.getStatus().getValue());
                    return response;
                }
        ).toList();

        if (designRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Design Request Empty")
                            .build()
            );
        }


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get list of Design Requests")
                        .data(designRequests)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getDesignRequestById(GetDesignRequestById request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find Design Request with " + request.getDesignId())
                            .build()
            );
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", designRequest.getId());
        map.put("school", designRequest.getSchool().getId());
        map.put("private", designRequest.isPrivate());
        map.put("feedback", designRequest.getFeedback().getId());
        map.put("status", designRequest.getStatus());


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get Design Request successfully")
                        .data(map)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest request) {

        Account account = accountRepo.findById(request.getSchoolId()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find Account with " + request.getSchoolId())
                            .build()
            );
        }

        DesignRequest designRequest = DesignRequest.builder()
                .creationDate(LocalDate.now())
                .school(account)
                .isPrivate(true)
                .status(Status.DESIGN_REQUEST_CREATED)
                .build();
        designRequestRepo.save(designRequest);

        for (CreateDesignRequest.Cloth cloth : request.getClothes()) {
            Cloth newCloth = clothRepo.save(Cloth.builder()
                    .type(cloth.getType())
                    .gender(cloth.getGender())
                    .category(cloth.getCategory())
                    .logoImage(cloth.getLogoImage())
                    .logoPosition(cloth.getLogoPosition())
                    .designRequest(designRequest)
                    .color(cloth.getColor())
                    .note(cloth.getNote())
                    .build());

            if (cloth.getDesignType().equals("TEMPLATE")) {

                Cloth template = clothRepo.findById(cloth.getTemplateId()).orElse(null);
                if (template == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            ResponseObject.builder()
                                    .message("Template Not Found")
                                    .build()
                    );
                }
                newCloth.setTemplate(template);
                clothRepo.save(newCloth);
            }

            if (cloth.getDesignType().equals("UPLOAD")) {
                createSampleImageByCloth(newCloth, cloth.getImages());
            }
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Create Design Request successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> pickPackage(PickPackageRequest request) {

        Packages packages = packageRepo.findById(request.getPackageId()).orElse(null);

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find Design Request with id " + request.getDesignRequestId())
                            .build()
            );
        }
        if (packages == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find package with id " + request.getPackageId())
                            .build()
            );
        }

        designRequest.setPkg(packages);
        designRequestRepo.save(designRequest);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Pick package successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> makeDesignPublic(MakeDesignPublicRequest request) {

        DesignRequest designRequest = designRequestRepo.findById(request.getDesignRequestId()).orElse(null);

        if (designRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find Design Request with id " + request.getDesignRequestId())
                            .build()
            );
        }

        designRequest.setPrivate(false);
        designRequestRepo.save(designRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Change design to public successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> createRevisionDesign(CreateRevisionDesignRequest request) {
        Optional<DesignDraft> optDraft = designDraftRepo.findByIdAndCloth_Id(request.getDesignDraftId(), request.getClothId());
        if (optDraft.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Design Draft does not belong to this cloth")
                            .build()
            );
        }
        DesignDraft draft = optDraft.get();

        if (designDraftRepo.existsByCloth_IdAndIsFinalTrue(request.getClothId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Cannot create revision request when one of them is final")
                            .build()
            );
        }

        RevisionRequest revisionRequest = RevisionRequest.builder()
                .designDraft(draft)
                .note(request.getNote())
                .build();
        revisionRequestRepo.save(revisionRequest);

        DesignComment sysComment = DesignComment.builder()
                .designRequest(optDraft.get().getCloth().getDesignRequest())
                .senderId(request.getSenderId())
                .senderRole(request.getSenderRole())
                .content("School requested a revision: " + request.getNote())
                .creationDate(LocalDateTime.now())
                .build();
        designCommentRepo.save(sysComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Create revision successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getAllDesignComments(int designId) {

        List<DesignComment> designComments = designCommentRepo.findAllByDesignRequest_Id(designId);

        if (designComments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .message("This Conversation are empty please wait designer submit first")
                            .build()
            );
        }

        List<Map<String, Object>> mapList = designComments.stream()
                .map(
                        comment ->{
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
                        .data(mapList)
                        .build()
        );
    }


    private void createSampleImageByCloth(Cloth cloth, List<CreateDesignRequest.Image> images) {
        for (CreateDesignRequest.Image image : images) {
            sampleImageRepo.save(
                    SampleImage.builder()
                            .imageUrl(image.getUrl())
                            .cloth(cloth)
                            .build());
        }
    }
}
