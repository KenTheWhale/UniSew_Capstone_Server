package com.unisew.server.repositories;

import com.unisew.server.models.DraftImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftImageRepo extends JpaRepository<DraftImage, Integer> {
}
