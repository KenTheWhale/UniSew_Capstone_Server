package com.unisew.server.repositories;

import com.unisew.server.models.Services;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepo extends JpaRepository<Services, Integer> {
}
