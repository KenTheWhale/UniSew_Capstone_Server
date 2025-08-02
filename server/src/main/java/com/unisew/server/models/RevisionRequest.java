package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`revision_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevisionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`delivery_id`")
    DesignDelivery designDelivery;

    @Column(name = "`request_date`")
    LocalDate requestDate;

    String note;

    @OneToOne(mappedBy = "revisionRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    DesignDelivery resultDelivery;
}
