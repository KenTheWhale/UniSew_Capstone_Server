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

    @OneToMany(mappedBy = "deliveryItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ItemImage> itemImages;
}
