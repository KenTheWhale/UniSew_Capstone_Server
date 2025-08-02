package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`delivery_item`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`delivery_id`")
    DesignDelivery designDelivery;

    @Column(name = "`design_item_id`")
    int designItemId;

    @Column(name = "`base_logo_height`")
    double baseLogoHeight;

    @Column(name = "`base_logo_width`")
    double baseLogoWidth;

    @Column(name = "`front_image_url`")
    String frontImageUrl;

    @Column(name = "`back_image_url`")
    String backImageUrl;

}
