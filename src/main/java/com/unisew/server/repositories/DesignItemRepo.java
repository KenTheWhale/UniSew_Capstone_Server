package com.unisew.server.repositories;

import com.unisew.server.models.DesignItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignItemRepo extends JpaRepository<DesignItem, Integer> {
    List<DesignItem> getAllByDesignRequest_Id(int id);
}
