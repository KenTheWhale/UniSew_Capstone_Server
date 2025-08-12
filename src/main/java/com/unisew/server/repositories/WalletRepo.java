package com.unisew.server.repositories;

import com.unisew.server.enums.Role;
import com.unisew.server.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepo extends JpaRepository<Wallet, Integer> {
    Wallet findByAccount_Role(Role role);

}
