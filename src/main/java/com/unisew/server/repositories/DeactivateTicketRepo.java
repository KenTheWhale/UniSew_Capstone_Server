package com.unisew.server.repositories;

import com.unisew.server.models.DeactivateTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeactivateTicketRepo extends JpaRepository<DeactivateTicket, Integer> {
}