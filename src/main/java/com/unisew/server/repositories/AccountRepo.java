package com.unisew.server.repositories;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailAndStatus(String email, Status status);

    boolean existsByEmail(String email);
}
