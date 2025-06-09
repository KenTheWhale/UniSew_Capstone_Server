package com.unisew.server.repositories;

import com.unisew.server.models.ReDesignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReDesignRequestRepo extends JpaRepository<ReDesignRequest, Integer> {
}
