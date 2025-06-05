package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`order`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`school_deadline`")
    LocalDate schoolDeadline;

    @Column(name = "`garment_deadline`")
    LocalDate garmentDeadline;

    long price;

    @Column(name = "`ship_fee`")
    long shipFee;

    @Column(name = "`service_fee`")
    long serviceFee;

    @Column(name = "`design_fee`")
    long designFee;

    @Column(name = "`order_date`")
    LocalDate orderDate;

    String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "`school_status`")
    Status schoolStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "`garment_status`")
    Status garmentStatus;

    @ManyToOne
    @JoinColumn(name = "`school_id`")
    Customer school;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`contract_id`")
    Contract contract;

    @OneToMany(mappedBy = "order")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SubOrder> subOrders;

    @OneToMany(mappedBy = "order")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<OrderDetail> orderDetails;
}
