package com.unisew.server.repositories;

import com.unisew.server.models.ContractRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRuleRepo extends JpaRepository<ContractRule, ContractRule.ID> {
}
