package com.unisew.server.repositories;

import com.unisew.server.models.Designer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignerRepo extends JpaRepository<Designer, Integer> {
}
