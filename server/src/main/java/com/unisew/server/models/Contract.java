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
@Table(name = "`contract`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`signing_date`")
    LocalDate signingDate;

    @Column(name = "`effective_date`")
    LocalDate effectiveDate;

    @Column(name = "`delivery_date`")
    LocalDate deliveryDate;

    @Column(name = "`total_price`")
    long totalPrice;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToOne(mappedBy = "contract", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GarmentContract garmentContract;

    @OneToOne(mappedBy = "contract", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    SchoolContract schoolContract;

    @OneToMany(mappedBy = "contract")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ContractRule> contractRules;
}
