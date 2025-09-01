package com.unisew.server.repositories;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppealRepo extends JpaRepository<Appeal, Integer> {
    boolean existsByFeedback_IdAndStatus(Integer feedback_id, Status status);
}
