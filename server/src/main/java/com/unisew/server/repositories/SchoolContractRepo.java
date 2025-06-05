package com.unisew.server.repositories;

import com.unisew.server.models.SchoolContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolContractRepo extends JpaRepository<SchoolContract, Integer> {
}
