package com.unisew.server.services.implementors;

import com.unisew.server.models.Cloth;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.SampleImage;
import com.unisew.server.repositories.ClothRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.SampleImageRepo;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.SampleImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SampleImageServiceImpl implements SampleImageService {

    final SampleImageRepo sampleImageRepo;
    final ClothRepo clothRepo;
    final DesignRequestRepo designRequestRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllSampleImages() {

        List<SampleImage> sampleImages = sampleImageRepo.findAll();


        if (sampleImages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    ResponseObject.builder()
                            .message("Sample images not found")
                            .build()
            );
        }

        List<Map<String, Object>> mappedSampleImages = sampleImages.stream()
                .map(sampleImage -> {

                    Cloth cloth = clothRepo.findById(sampleImage.getCloth().getId()).orElse(null);
                    assert cloth != null;
                    DesignRequest designRequest = designRequestRepo.findById(cloth.getDesignRequest().getId()).orElse(null);
                    assert designRequest != null;
                    Map<String, Object> mappedDesign = new HashMap<>();
                    mappedDesign.put("id", designRequest.getId());
                    mappedDesign.put("createDate", designRequest.getCreationDate());
                    mappedDesign.put("isPrivate", designRequest.isPrivate());

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", sampleImage.getId());
                    map.put("clothId", sampleImage.getCloth().getId());
                    map.put("url", sampleImage.getImageUrl());
                    map.put("designRequest", mappedDesign);
                    return map;
                }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Sample images found")
                        .data(mappedSampleImages)
                        .build()
        );
    }
}
