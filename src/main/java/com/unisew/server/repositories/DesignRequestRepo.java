package com.unisew.server.repositories;

import com.unisew.server.models.DesignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignRequestRepo extends JpaRepository<DesignRequest, Integer> {
}
