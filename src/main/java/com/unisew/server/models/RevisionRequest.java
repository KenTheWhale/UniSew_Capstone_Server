package com.unisew.server.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
    LocalDateTime requestDate;

    String note;

    @OneToOne(mappedBy = "revisionRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    DesignDelivery resultDelivery;
}
