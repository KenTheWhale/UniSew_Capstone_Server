package com.unisew.server.repositories;

import com.unisew.server.enums.ItemCategory;
import com.unisew.server.models.Fabric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FabricRepo extends JpaRepository<Fabric,Integer> {
}
