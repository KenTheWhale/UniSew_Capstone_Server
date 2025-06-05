package com.unisew.server.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "`customer`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {

    @Id
    @Column(name = "`account_id`")
    Integer id;

    String phone;

    String street;

    String ward;

    String district;

    String province;

    @Column(name = "`tax_code`")
    String taxCode;

    @Column(name = "`violations_times`")
    int violationsTimes;

    boolean limited;

    @Column(name = "`end_limitation_date`")
    LocalDate endLimitationDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "`account_id`")
    Account account;

    @OneToOne(mappedBy = "garment", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GarmentDeclaration garmentDeclaration;

    @OneToMany(mappedBy = "school")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignRequest> designRequests;

    @OneToMany(mappedBy = "school")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Order> orders;

    @OneToMany(mappedBy = "garment")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SubOrder> subOrders;
}
