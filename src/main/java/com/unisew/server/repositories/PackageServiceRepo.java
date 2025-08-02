package com.unisew.server.repositories;

import com.unisew.server.models.PackageService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageServiceRepo extends JpaRepository<PackageService, Integer> {
}
