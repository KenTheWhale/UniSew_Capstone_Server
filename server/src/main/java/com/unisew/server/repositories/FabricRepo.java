package com.unisew.server.repositories;

import com.unisew.server.models.Fabric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface FabricRepo extends JpaRepository<Fabric,Integer> {
}
