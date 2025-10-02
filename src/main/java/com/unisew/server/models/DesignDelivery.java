package com.unisew.server.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`design_delivery`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;

    @OneToMany(mappedBy = "designDelivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<RevisionRequest> revisionRequests;

    @OneToOne
    @JoinColumn(name = "`revision_id`")
    RevisionRequest revisionRequest;

    String name;

    int version;

    @Column(name = "`submit_date`")
    LocalDateTime submitDate;

    @Column(name = "`is_revision`")
    boolean revision;

    String note;

    @OneToOne(mappedBy = "designDelivery", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    SchoolDesign schoolDesign;

    @OneToMany(mappedBy = "designDelivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DeliveryItem> deliveryItems;

}
