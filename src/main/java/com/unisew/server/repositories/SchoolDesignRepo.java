package com.unisew.server.repositories;

import com.unisew.server.models.SchoolDesign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolDesignRepo extends JpaRepository<SchoolDesign, Integer> {
    boolean existsByCustomer_IdAndDesignDelivery_Id(Integer customerId, Integer designDeliveryId);

    List<SchoolDesign> findAllByCustomer_Account_Id(Integer customerAccountId);

    Optional<SchoolDesign> findByDesignDelivery_DesignRequest_Id(int requestId);

    Optional<SchoolDesign> findByDesignDelivery_Id(int deliveryId);
}
