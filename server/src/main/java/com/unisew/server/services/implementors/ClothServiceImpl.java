package com.unisew.server.services.implementors;

import com.unisew.server.models.Cloth;
import com.unisew.server.repositories.ClothRepo;
import com.unisew.server.repositories.SampleImageRepo;
import com.unisew.server.requests.GetAllClothByRequestId;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.ClothService;
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
public class ClothServiceImpl implements ClothService {

    final ClothRepo clothRepo;
    final SampleImageRepo sampleImageRepo;

    @Override
    public ResponseEntity<ResponseObject> getAllClothesByRequestId(GetAllClothByRequestId request) {

        List<Cloth> clothList = clothRepo.getAllByDesignRequest_Id(request.getRequestId());

        if (clothList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    ResponseObject.builder()
                            .message("no cloth found")
                            .build()
            );
        }

        List<Map<String, Object>> mappedCloths = clothList.stream().map(cloth -> {
            Map<String, Object> map = new HashMap<>();
            map.put("logo_height", cloth.getLogoHeight());
            map.put("logo_width", cloth.getLogoWidth());
            map.put("template_id", cloth.getTemplate() != null ? cloth.getTemplate().getId() : null);
            map.put("cloth_category", cloth.getCategory());
            map.put("cloth_type", cloth.getType());
            map.put("color", cloth.getColor());
            map.put("fabric", cloth.getFabric());
            map.put("logo_image", cloth.getLogoImage());
            map.put("logo_position", cloth.getLogoPosition());
            map.put("note", cloth.getNote());
            return map;
        }).toList();


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Cloth list found")
                        .data(mappedCloths)
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getAllClothes() {

        List<Cloth> cloths = clothRepo.findAll();

        if (cloths.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    ResponseObject.builder()
                            .message("no cloth found")
                            .build()
            );
        }

        List<Map<String, Object>> mappedCloths = cloths.stream()
                .map(cloth -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("logo_height", cloth.getLogoHeight());
                    map.put("logo_width", cloth.getLogoWidth());
                    map.put("template_id", cloth.getTemplate() != null ? cloth.getTemplate().getId() : null);
                    map.put("cloth_category", cloth.getCategory());
                    map.put("cloth_type", cloth.getType());
                    map.put("color", cloth.getColor());
                    map.put("fabric", cloth.getFabric());
                    map.put("logo_image", cloth.getLogoImage());
                    map.put("logo_position", cloth.getLogoPosition());
                    map.put("note", cloth.getNote());
                    return map;
                }).toList();


        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Cloth list found")
                        .data(mappedCloths)
                        .build()
        );
    }
}
