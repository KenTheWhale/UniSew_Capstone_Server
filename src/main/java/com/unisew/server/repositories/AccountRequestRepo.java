package com.unisew.server.repositories;

import com.unisew.server.models.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRequestRepo extends JpaRepository<AccountRequest, Integer> {
    boolean existsByEmail(String email);

    Optional<AccountRequest> findByEmail(String email);
}
