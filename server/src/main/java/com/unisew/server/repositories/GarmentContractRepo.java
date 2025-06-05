package com.unisew.server.repositories;

import com.unisew.server.models.GarmentContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarmentContractRepo extends JpaRepository<GarmentContract, Integer> {
}
