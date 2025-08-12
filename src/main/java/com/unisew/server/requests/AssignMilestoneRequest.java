package com.unisew.server.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignMilestoneRequest {

    Integer orderId;
    List<Phase> phaseList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Phase {
        Integer id;
        int stage;
        LocalDate startDate;
        LocalDate endDate;
    }
}
