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
