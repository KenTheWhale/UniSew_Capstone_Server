package com.unisew.server.repositories;

import com.unisew.server.models.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepo extends JpaRepository<Milestone, Integer> {
    List<Milestone> findAllByPhase_Id(Integer phaseId);
    List<Milestone> findAllByOrder_Id(Integer orderId);
}
