package com.unisew.server.repositories;

import com.unisew.server.models.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothRepo extends JpaRepository<Cloth, Integer> {
    List<Cloth> getAllByDesignRequest_Id(int id);
}
