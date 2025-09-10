package com.unisew.server.repositories;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailAndStatus(String email, Status status);

    boolean existsByEmailOrCustomer_Phone(String email, String phone);

    boolean existsByEmail(String email);

    boolean existsByCustomer_TaxCode(String taxCode);

    List<Account> findAllByRegisterDateBetween(LocalDate from, LocalDate to);

    long countByStatus(Status status);

    long countByRole(Role role);

    Account findByCustomer_Partner_Id(Integer customerPartnerId);
}
