package com.unisew.server.repositories;

import com.unisew.server.models.Packages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackagesRepo extends JpaRepository<Packages, Integer> {
    List<Packages> findAllByDesigner_Customer_Account_Id(Integer designerCustomerAccountId);
}
