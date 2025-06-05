package com.unisew.server.repositories;

import com.unisew.server.models.DefectiveOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefectiveOrderDetailRepo extends JpaRepository<DefectiveOrderDetail, Integer> {
}
