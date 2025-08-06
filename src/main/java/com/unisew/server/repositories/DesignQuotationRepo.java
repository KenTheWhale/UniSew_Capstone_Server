package com.unisew.server.repositories;

import com.unisew.server.models.DesignQuotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignQuotationRepo extends JpaRepository<DesignQuotation, Integer> {
    List<DesignQuotation> findAllByDesigner_Customer_Account_Id(Integer designerCustomerAccountId);
}
