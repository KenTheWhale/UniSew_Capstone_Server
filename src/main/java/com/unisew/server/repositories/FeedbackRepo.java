package com.unisew.server.repositories;

import com.unisew.server.models.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {
    Feedback findByOrder_Id(Integer orderId);

    List<Feedback> findAllByReportIsTrue();
}
