package com.unisew.server.repositories;

import com.unisew.server.models.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepo extends JpaRepository<Services, Integer> {

    List<Services> findAllByPackageServices_Pkg_Id(int packageId);
}
