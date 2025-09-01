package com.unisew.server.repositories;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Appeals;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppealsRepo extends JpaRepository<Appeals, Integer> {
    boolean existsByFeedback_IdAndStatus(Integer feedback_id, Status status);
}
