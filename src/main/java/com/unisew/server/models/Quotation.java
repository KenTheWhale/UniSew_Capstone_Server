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
@Table(name = "`quotation`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`order_id`")
    Order order;

    @ManyToOne
    @JoinColumn(name = "`garment_id`")
    Partner garment;

    @Column(name = "`early_delivery_date`")
    LocalDate earlyDeliveryDate;

    @Column(name = "`acceptance_deadline`")
    LocalDate acceptanceDeadline;

    long price;

    String note;

    @Enumerated(EnumType.STRING)
    Status status;

}
