package com.unisew.server.repositories;

import com.unisew.server.models.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationRepo extends JpaRepository<Quotation, Integer> {
}
