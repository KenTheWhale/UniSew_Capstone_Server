package com.unisew.server.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiveFeedbackRequest {
    Integer requestId;
    Integer orderId;
    Integer rating;
    String content;
    boolean report;
    List<String> imageUrls;
    String videoUrl;
    LocalDate appealsDeadline;
}
