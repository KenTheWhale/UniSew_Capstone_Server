package com.unisew.server.repositories;

import com.unisew.server.enums.PaymentType;
import com.unisew.server.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByOrderByIdDesc();
    List<Transaction> findAllByCreationDateBetween(LocalDate from, LocalDate to);
    List<Transaction> findAllByItemId(Integer itemId);
    List<Transaction> findAllByWallet_Id(Integer walletId);
    List<Transaction> findAllBySender_IdOrReceiver_Id(int senderId, int receiverId);
    List<Transaction> findAllByItemIdAndPaymentType(Integer itemId, PaymentType paymentType);

}
