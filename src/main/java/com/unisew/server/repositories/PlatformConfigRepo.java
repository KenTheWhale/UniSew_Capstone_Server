package com.unisew.server.repositories;

import com.unisew.server.models.PlatformConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformConfigRepo extends JpaRepository<PlatformConfig, Integer> {
}
