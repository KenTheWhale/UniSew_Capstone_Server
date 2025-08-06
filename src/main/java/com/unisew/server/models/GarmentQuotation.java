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
@Table(name = "`quotation`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GarmentQuotation {

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
