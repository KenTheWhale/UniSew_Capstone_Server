package com.unisew.server.repositories;

import com.unisew.server.models.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepo extends JpaRepository<Milestone, Integer> {
}
