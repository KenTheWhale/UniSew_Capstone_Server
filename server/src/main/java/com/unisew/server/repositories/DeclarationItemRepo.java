package com.unisew.server.repositories;

import com.unisew.server.models.DeclarationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeclarationItemRepo extends JpaRepository<DeclarationItem, Integer> {
}
