package com.unisew.server.repositories;

import com.unisew.server.models.SubOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubOrderRepo extends JpaRepository<SubOrder, Integer> {
}
