package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`school_design`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SchoolDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
    @JoinColumn(name = "`delivery_id`")
    DesignDelivery designDelivery;

    @ManyToOne
    @JoinColumn(name = "`fabric_id`")
    Fabric fabric;

    @ManyToOne
    @JoinColumn(name = "`school_id`")
    Customer customer;

    @OneToMany(mappedBy = "schoolDesign", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Order> orders;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignRequest> designRequests;

}
