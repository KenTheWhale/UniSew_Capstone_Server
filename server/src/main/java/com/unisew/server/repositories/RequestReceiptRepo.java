package com.unisew.server.repositories;

import com.unisew.server.models.RequestReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestReceiptRepo extends JpaRepository<RequestReceipt, Integer> {
}
