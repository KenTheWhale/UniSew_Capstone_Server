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

    @ManyToOne
    @JoinColumn(name = "`school_design_id`")
    SchoolDesign schoolDesign;

    @OneToOne
    @JoinColumn(name = "`feedback_id`")
    Feedback feedback;

    Integer garmentId;

    @Column(name = "`garment_name`")
    String garmentName;

    LocalDate deadline;

    long price;

    @Column(name = "`shipping_fee`")
    long shippingFee;

    @Column(name = "`shipping_code`")
    String shippingCode;

    @Column(name = "`order_date`")
    LocalDate orderDate;

    String note;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToMany(mappedBy = "order")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<GarmentQuotation> garmentQuotations;

    @OneToMany(mappedBy = "order")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Milestone> milestones;

}
