package com.unisew.server.repositories;

import com.unisew.server.models.PackageRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRuleRepo extends JpaRepository<PackageRule, Integer> {
}
