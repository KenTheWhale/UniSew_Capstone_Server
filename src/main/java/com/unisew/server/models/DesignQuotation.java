package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "`design_quotation`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignQuotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`designer_id`")
    Partner designer;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;

    @Column(name = "`note`")
    String note;

    @Column(name = "`delivery_with_in`")
    int deliveryWithIn;

    @Column(name = "`revision_time`")
    int revisionTime;

    @Column(name = "`extra_revision_price`")
    long extraRevisionPrice;

    long price;

    @Column(name = "`acceptance_deadline`")
    LocalDate acceptanceDeadline;

    @Enumerated(EnumType.STRING)
    Status status;
}
