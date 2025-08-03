package com.unisew.server.repositories;

import com.unisew.server.models.RequestReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestReceiptRepo extends JpaRepository<RequestReceipt, Integer> {
    List<RequestReceipt> findAllByDesignRequest_Id(Integer designRequestId);
}
