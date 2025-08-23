package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`partner`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
    @JoinColumn(name = "`customer_id`")
    Customer customer;

    @Column(name = "`outside_preview`")
    String outsidePreview;

    @Column(name = "`inside_preview`")
    String insidePreview;

    @Column(name = "`shipping_uid`")
    String shippingUid;

    @Column(name = "`start_time`")
    LocalTime startTime;

    @Column(name = "`end_time`")
    LocalTime endTime;

    Integer rating;

    @Column(name = "`is_busy`")
    boolean busy;

    @OneToMany(mappedBy = "designer", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignQuotation> designQuotations;

    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ThumbnailImage> thumbnailImages;

    @OneToMany(mappedBy = "garment", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<GarmentQuotation> garmentQuotations;

    @OneToMany(mappedBy = "garment", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SewingPhase> sewingPhases;
}
