package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`sample_image`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SampleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`image_url`")
    String imageUrl;

    @ManyToOne
    @JoinColumn(name = "`design_item_id`")
    DesignItem designItem;
}
