package com.unisew.server.repositories;

import com.unisew.server.models.DefectiveOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefectiveOrderRepo extends JpaRepository<DefectiveOrder, Integer> {
}
