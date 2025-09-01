package com.unisew.server.repositories;

import com.unisew.server.models.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppealRepo extends JpaRepository<Appeal, Integer> {
}
