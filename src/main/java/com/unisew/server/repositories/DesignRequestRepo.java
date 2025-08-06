package com.unisew.server.repositories;

import com.unisew.server.models.DesignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignRequestRepo extends JpaRepository<DesignRequest, Integer> {
    List<DesignRequest> findAllBySchool_Id(Integer schoolId);

    List<DesignRequest> findAllByDesignQuotationId(Integer quotationId);
}
