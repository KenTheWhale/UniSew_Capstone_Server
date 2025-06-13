package com.unisew.server.repositories;

import com.unisew.server.models.DesignDraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignDraftRepo extends JpaRepository<DesignDraft, Integer> {
}
