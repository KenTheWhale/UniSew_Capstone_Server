package com.unisew.server.repositories;

import com.unisew.server.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
}
