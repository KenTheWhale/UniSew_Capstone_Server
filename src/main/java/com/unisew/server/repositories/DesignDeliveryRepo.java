package com.unisew.server.repositories;

import com.unisew.server.models.DesignDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignDeliveryRepo extends JpaRepository<DesignDelivery, Integer> {
    List<DesignDelivery> findAllByDesignRequest_Id(Integer designRequestId);
}
