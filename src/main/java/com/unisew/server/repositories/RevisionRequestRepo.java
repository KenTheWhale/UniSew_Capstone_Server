package com.unisew.server.repositories;

import com.unisew.server.models.RevisionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RevisionRequestRepo extends JpaRepository<RevisionRequest, Integer> {
    List<RevisionRequest> findAllByDesignDelivery_DesignRequest_Id(Integer designDeliveryDesignRequestId);
}
