package com.unisew.server.repositories;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailAndStatus(String email, Status status);

    boolean existsByEmail(String email);

    List<Account> findAllByRegisterDateBetween(LocalDate from, LocalDate to);

    long countByStatus(Status status);

    long countByRole(Role role);
}
