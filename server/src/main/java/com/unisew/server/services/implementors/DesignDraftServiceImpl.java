package com.unisew.server.services.implementors;

import com.unisew.server.models.Cloth;
import com.unisew.server.models.DesignComment;
import com.unisew.server.models.DesignDraft;
import com.unisew.server.models.DraftImage;
import com.unisew.server.repositories.ClothRepo;
import com.unisew.server.repositories.DesignCommentRepo;
import com.unisew.server.repositories.DesignDraftRepo;
import com.unisew.server.repositories.DraftImageRepo;
import com.unisew.server.requests.CreateDesignDraftRequest;
import com.unisew.server.requests.SetDesignDraftFinalRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignDraftService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignDraftServiceImpl implements DesignDraftService {

    final ClothRepo clothRepo;
    final DesignDraftRepo designDraftRepo;
    final DraftImageRepo draftImageRepo;
    final DesignCommentRepo designCommentRepo;

    @Override
    public ResponseEntity<ResponseObject> createDesignDraft(CreateDesignDraftRequest request) {

        Cloth cloth = clothRepo.findById(request.getClothId()).orElse(null);


        if (cloth == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find cloth with id " + request.getClothId())
                            .build()
            );
        }

        DesignDraft designDraft = DesignDraft.builder()
                .cloth(cloth)
                .description(request.getDescriptions())
                .designDate(LocalDate.now())
                .isFinal(false)
                .build();
        designDraftRepo.save(designDraft);

        List<DraftImage> draftImages = request.getImages().stream()
                .map(image -> DraftImage.builder()
                        .imageUrl(image.getUrl())
                        .name(image.getName())
                        .designDraft(designDraft)
                        .build()
                ).toList();
        draftImageRepo.saveAll(draftImages);

        designDraft.setDraftImages(draftImages);
        designDraftRepo.save(designDraft);

        DesignComment systemComment = DesignComment.builder()
                .designRequest(cloth.getDesignRequest())
                .senderId(0)
                .senderRole("system")
                .content("Designer delivered a new design version " )
                .creationDate(LocalDateTime.now())
                .build();
        designCommentRepo.save(systemComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Upload Design draft successful")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> setDesignDraftFinal(SetDesignDraftFinalRequest request) {

        DesignDraft designDraft = designDraftRepo.findById(request.getDesignDraftId()).orElse(null);

        if (designDraft == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find design draft with id " + request.getDesignDraftId())
                            .build()
            );
        }

            designDraft.setFinal(true);
            designDraftRepo.save(designDraft);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Make design draft final successful")
                        .build()
        );
    }
}
