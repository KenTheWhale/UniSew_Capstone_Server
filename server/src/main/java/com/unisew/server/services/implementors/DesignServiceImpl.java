package com.unisew.server.services.implementors;

import com.unisew.server.enums.ClothCategory;
import com.unisew.server.enums.ClothType;
import com.unisew.server.models.Fabric;
import com.unisew.server.repositories.FabricRepo;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.DesignService;
import com.unisew.server.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DesignServiceImpl implements DesignService {

    private final FabricRepo fabricRepo;

    //-----------------------------------FABRIC---------------------------------------//
    @Override
    public ResponseEntity<ResponseObject> getAllFabric() {

        List<Fabric> fabrics = fabricRepo.findAll();

        Map<String, Object> response = new HashMap<>();

        for (ClothCategory category : ClothCategory.values()) {
            List<Fabric> categoryFabric = fabrics.stream()
                    .filter(fabric -> fabric.getClothCategory().equals(category))
                    .toList();

            Map<String, Object> categoryMap = new HashMap<>();

            List<Map<String, Object>> shirts = categoryFabric.stream()
                    .filter(f -> f.getClothType().equals(ClothType.SHIRT))
                    .map(this::mapFabric)
                    .toList();

            List<Map<String, Object>> pants = categoryFabric.stream()
                    .filter(f -> f.getClothType().equals(ClothType.PANTS))
                    .map(this::mapFabric)
                    .toList();

            List<Map<String, Object>> skirts = categoryFabric.stream()
                    .filter(f -> f.getClothType().equals(ClothType.SKIRT))
                    .map(this::mapFabric)
                    .toList();

            categoryMap.put("shirts", shirts);
            categoryMap.put("pants", pants);
            categoryMap.put("skirts", skirts);

            response.put(category.name().toLowerCase(), categoryMap);

        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List Fabric")
                        .body(response)
                        .build()
        );
    }


    private Map<String, Object> mapFabric(Fabric fabric){
        Map<String, Object> map = new HashMap<>();
        map.put("id", fabric.getId());
        map.put("name", fabric.getName());
        map.put("description", fabric.getDescription());
        return map;
    }
}
