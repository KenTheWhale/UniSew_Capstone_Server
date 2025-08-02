package com.unisew.server.repositories;

import com.unisew.server.models.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryItemRepo extends JpaRepository<DeliveryItem, Integer> {
}
