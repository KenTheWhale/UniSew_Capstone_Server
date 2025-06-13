package com.unisew.server.repositories;

import com.unisew.server.models.FeedbackImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackImageRepo extends JpaRepository<FeedbackImage, Integer> {
}
