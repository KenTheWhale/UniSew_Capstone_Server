package com.unisew.server.repositories;

import com.unisew.server.models.DesignComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignCommentRepo extends JpaRepository<DesignComment, Integer> {
    List<DesignComment> findAllByDesignRequest_Id(int designId);
}
