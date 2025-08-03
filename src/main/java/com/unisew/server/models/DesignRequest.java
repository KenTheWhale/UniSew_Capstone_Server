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

    @ManyToOne
    @JoinColumn(name = "`template_id`")
    SchoolDesign template;

    Integer packageId;

    String name;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Column(name = "`logo_image`")
    String logoImage;

    @Column(name = "`is_private`")
    boolean privacy;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`package_name`")
    String packageName;

    @Column(name = "`package_header_content`")
    String headerContent;

    @Column(name = "`package_delivery_within`")
    Integer packageDeliveryWithin;

    @Column(name = "`revision_time`")
    Integer revisionTime;

    @Column(name = "`package_price`")
    long packagePrice;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<RequestReceipt> requestReceipts;

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
    List<DesignComment> designComments;


}
