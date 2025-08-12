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
    List<Integer> phaseIdList;
    Integer stage;
    LocalDate startDate;
    LocalDate endDate;
}
