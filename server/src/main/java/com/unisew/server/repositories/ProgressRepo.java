package com.unisew.server.repositories;

import com.unisew.server.models.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressRepo extends JpaRepository<Progress, Integer> {
}
