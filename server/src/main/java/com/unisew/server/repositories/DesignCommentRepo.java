package com.unisew.server.repositories;

import com.unisew.server.models.DesignComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignCommentRepo extends JpaRepository<DesignComment, Integer> {
}
