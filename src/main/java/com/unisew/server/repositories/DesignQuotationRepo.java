package com.unisew.server.repositories;

import com.unisew.server.enums.Status;
import com.unisew.server.models.DesignQuotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignQuotationRepo extends JpaRepository<DesignQuotation, Integer> {
    List<DesignQuotation> findAllByDesigner_Customer_Account_Id(Integer designerCustomerAccountId);

    boolean existsByDesigner_IdAndDesignRequest_IdAndStatus(int designerId, int requestId, Status status);

    List<DesignQuotation> findAllByDesigner_Id(Integer designerId);

    boolean existsByDesigner_Customer_Account_IdAndDesignRequest_Id(Integer id, Integer id1);
}
