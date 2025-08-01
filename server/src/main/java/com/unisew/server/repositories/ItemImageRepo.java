package com.unisew.server.repositories;

import com.unisew.server.models.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepo extends JpaRepository<ItemImage, Integer> {
}
