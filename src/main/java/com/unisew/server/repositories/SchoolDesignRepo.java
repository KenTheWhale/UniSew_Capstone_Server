package com.unisew.server.repositories;

import com.unisew.server.models.SchoolDesign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolDesignRepo extends JpaRepository<SchoolDesign, Integer> {
    boolean existsByCustomer_IdAndDesignDelivery_Id(Integer customerId, Integer designDeliveryId);

    List<SchoolDesign> findAllByCustomer_Account_Id(Integer customerAccountId);
    
    boolean existsByDesignDelivery_Id(Integer designDeliveryId);
}
