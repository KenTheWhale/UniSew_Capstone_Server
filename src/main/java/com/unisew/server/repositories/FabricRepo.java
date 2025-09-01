package com.unisew.server.repositories;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.models.Fabric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FabricRepo extends JpaRepository<Fabric, Integer> {
    Optional<Fabric> findByNameAndDesignItemTypeAndDesignItemCategory(String name, DesignItemType type, DesignItemCategory category);
}
