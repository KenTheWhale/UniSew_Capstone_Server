package com.unisew.server.repositories;

import com.unisew.server.models.SampleImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleImageRepo extends JpaRepository<SampleImage, Integer> {
}
