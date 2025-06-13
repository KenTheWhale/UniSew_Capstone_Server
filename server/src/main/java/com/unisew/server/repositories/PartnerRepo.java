package com.unisew.server.repositories;

import com.unisew.server.models.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepo extends JpaRepository<Partner, Integer> {
}
