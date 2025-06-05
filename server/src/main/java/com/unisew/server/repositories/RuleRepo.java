package com.unisew.server.repositories;

import com.unisew.server.models.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepo extends JpaRepository<Rule, Integer> {
}
