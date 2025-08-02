package com.unisew.server.repositories;

import com.unisew.server.models.RevisionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisionRequestRepo extends JpaRepository<RevisionRequest, Integer> {
}
