package com.unisew.server.repositories;

import com.unisew.server.models.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepo extends JpaRepository<Contract, Integer> {
}
