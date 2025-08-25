package com.unisew.server.repositories;

import com.unisew.server.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findAllByGarmentQuotations_Garment_Id(Integer garmentQuotations_Garment_Id);

    List<Order> findAllByGarmentId(Integer garmentId);

    Optional<Order> findByIdAndSchoolDesign_Customer_Account_Id(int orderId, int accountId);
}
