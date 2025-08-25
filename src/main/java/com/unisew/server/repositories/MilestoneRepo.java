package com.unisew.server.repositories;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MilestoneRepo extends JpaRepository<Milestone, Integer> {
    List<Milestone> findAllByPhase_Id(Integer phaseId);

    List<Milestone> findAllByOrder_Id(Integer orderId);

    Optional<Milestone> findByOrder_IdAndStatus(int orderId, Status status);

    Optional<Milestone> findByOrder_IdAndStage(int orderId, int stage);
}
