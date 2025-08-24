package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`milestone`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`phase_id`")
    SewingPhase phase;

    @ManyToOne
    @JoinColumn(name = "`order_id`")
    Order order;

    @Column(name = "`start_date`")
    LocalDate startDate;

    @Column(name = "`end_date`")
    LocalDate endDate;

    @Column(name = "`completed_date`")
    LocalDate completedDate;

    @Column(name = "`image_url`")
    String imgUrl;

    @Enumerated(EnumType.STRING)
    Status status;

    int stage;
}
