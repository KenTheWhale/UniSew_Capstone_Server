package com.unisew.server.repositories;

import com.unisew.server.enums.ClothCategory;
import com.unisew.server.models.Fabric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FabricRepo extends JpaRepository<Fabric,Integer> {
    List<Fabric> findAllByClothCategory(ClothCategory clothCategory);
}
