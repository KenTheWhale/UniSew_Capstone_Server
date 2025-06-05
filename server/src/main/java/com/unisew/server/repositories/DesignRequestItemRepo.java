package com.unisew.server.repositories;

import com.unisew.server.models.DesignRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignRequestItemRepo extends JpaRepository<DesignRequestItem, Integer> {
}
