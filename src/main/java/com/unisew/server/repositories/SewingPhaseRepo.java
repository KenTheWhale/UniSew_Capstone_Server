package com.unisew.server.repositories;

import com.unisew.server.enums.Status;
import com.unisew.server.models.SewingPhase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SewingPhaseRepo extends JpaRepository<SewingPhase, Integer> {

    Integer countSewingPhaseByGarment_IdAndStatus(Integer garmentId, Status status);
}
