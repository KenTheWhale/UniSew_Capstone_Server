package com.unisew.server.repositories;

import com.unisew.server.models.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawRequestRepo extends JpaRepository<WithdrawRequest, Integer> {
}
