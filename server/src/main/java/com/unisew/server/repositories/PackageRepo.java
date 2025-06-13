package com.unisew.server.repositories;

import com.unisew.server.models.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepo extends JpaRepository<Package, Integer> {
}
