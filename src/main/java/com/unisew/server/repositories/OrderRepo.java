package com.unisew.server.repositories;

import com.unisew.server.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Integer> {
}
