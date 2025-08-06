package com.unisew.server.repositories;

import com.unisew.server.models.GarmentQuotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GarmentQuotationRepo extends JpaRepository<GarmentQuotation, Integer> {
    List<GarmentQuotation> findAllByOrder_Id(Integer orderId);
}
