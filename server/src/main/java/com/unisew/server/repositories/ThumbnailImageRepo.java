package com.unisew.server.repositories;

import com.unisew.server.models.ThumbnailImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailImageRepo extends JpaRepository<ThumbnailImage, Integer> {
}
