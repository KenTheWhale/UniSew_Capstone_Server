package com.unisew.server.repositories;

import com.unisew.server.models.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawRequestRepo extends JpaRepository<WithdrawRequest, Integer> {
    List<WithdrawRequest> findAllByWallet_Account_Id(Integer walletAccountId);
}
