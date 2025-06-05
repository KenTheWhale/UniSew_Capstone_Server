package com.unisew.server.repositories;

import com.unisew.server.models.SubOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubOrderDetailRepo extends JpaRepository<SubOrderDetail, Integer> {
}
