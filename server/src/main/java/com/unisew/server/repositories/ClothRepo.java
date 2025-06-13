package com.unisew.server.repositories;

import com.unisew.server.models.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothRepo extends JpaRepository<Cloth, Integer> {
}
