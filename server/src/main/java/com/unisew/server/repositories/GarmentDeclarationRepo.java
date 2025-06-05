package com.unisew.server.repositories;

import com.unisew.server.models.GarmentDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarmentDeclarationRepo extends JpaRepository<GarmentDeclaration, Integer> {
}
