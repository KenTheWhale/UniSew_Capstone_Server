package com.unisew.server.repositories;

import com.unisew.server.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByOrderByIdDesc();
    List<Transaction> findAllByCreationDateBetween(LocalDate from, LocalDate to);
}
