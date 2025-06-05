package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`sub_order`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`start_date`")
    LocalDate startDate;

    @OneToOne(mappedBy = "subOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GarmentContract garmentContract;

    @ManyToOne
    @JoinColumn(name = "`order_id`")
    Order order;

    @OneToMany(mappedBy = "subOrder")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SubOrderDetail> subOrderDetails;

    @OneToMany(mappedBy = "subOrder")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DefectiveOrder> defectiveOrders;
}
