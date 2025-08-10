package com.unisew.server.repositories;

import com.unisew.server.models.DesignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignRequestRepo extends JpaRepository<DesignRequest, Integer> {
    List<DesignRequest> findAllBySchool_Id(int schoolId);

    Optional<DesignRequest> findByDesignQuotationId(int quotationId);
}
