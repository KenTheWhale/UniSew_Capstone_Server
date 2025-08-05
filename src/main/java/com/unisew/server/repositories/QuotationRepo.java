package com.unisew.server.repositories;

import com.unisew.server.models.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuotationRepo extends JpaRepository<Quotation, Integer> {
    List<Quotation> findAllByOrder_Id(Integer orderId);
}
