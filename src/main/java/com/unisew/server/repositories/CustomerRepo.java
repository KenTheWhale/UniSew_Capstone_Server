package com.unisew.server.repositories;

import com.unisew.server.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {

    boolean existsByTaxCode(String taxCode);

    boolean existsByPhone(String phone);

    boolean existsByAddress(String address);

    Optional<Customer> findByBusinessName(String businessName);
}
