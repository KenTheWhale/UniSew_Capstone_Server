package com.unisew.server.repositories;

import com.unisew.server.models.RequestedImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestedImageRepo extends JpaRepository<RequestedImage, Integer> {
}
