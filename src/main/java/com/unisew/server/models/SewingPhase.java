package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`sewing_phase`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SewingPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`garment_id`")
    Partner garment;

    String name;

    String description;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToMany(mappedBy = "phase")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Milestone> milestones;
}
