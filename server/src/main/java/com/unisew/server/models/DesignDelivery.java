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

    @Column(name = "`file_url`")
    String fileUrl;

    int code;

    @Column(name = "`submit_date`")
    LocalDate submitDate;

    @Column(name = "`is_revision`")
    boolean revision;

    String note;

    @OneToOne(mappedBy = "schoolDesign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    SchoolDesign schoolDesign;

    @OneToMany(mappedBy = "designDelivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DeliveryItem> deliveryItems;

}
