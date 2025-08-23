package com.unisew.server.models;

import com.unisew.server.enums.Status;
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
@Table(name = "`design_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`school_id`")
    Customer school;

    @OneToOne
    @JoinColumn(name = "`feedback_id`")
    Feedback feedback;

    @Column(name = "`design_quotation_id`")
    Integer designQuotationId;

    String name;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Column(name = "`logo_image`")
    String logoImage;

    long price;

    @Column(name = "`is_private`")
    boolean privacy;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`revision_time`")
    Integer revisionTime;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignDelivery> designDeliveries;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignItem> designItems;


    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignQuotation> designQuotations;
}
