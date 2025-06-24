package com.unisew.server.repositories;

import com.unisew.server.models.DesignDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignDraftRepo extends JpaRepository<DesignDraft, Integer> {
    Optional<DesignDraft> findByIdAndCloth_Id(Integer id, Integer cloth_id);

    boolean existsByCloth_IdAndIsFinalTrue(Integer cloth_id);
}
