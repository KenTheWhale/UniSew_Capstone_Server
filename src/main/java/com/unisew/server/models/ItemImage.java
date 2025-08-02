package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`item_image`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`delivery_item_id`")
    DeliveryItem deliveryItem;

    @Column(name = "`image_url`")
    String imageUrl;

    String name;
}
