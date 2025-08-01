package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`thumbnail_image`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThumbnailImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`partner_id`")
    Partner partner;

    @Column(name = "`image_url`")
    String imageUrl;

}
