package com.unisew.server.repositories;

import com.unisew.server.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findAllByGarmentQuotations_Garment_Id(Integer garmentQuotations_Garment_Id);

    List<Order> findAllByGarmentId(Integer garmentId);
}
