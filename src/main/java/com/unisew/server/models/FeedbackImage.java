package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`feedback_image`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`image_url`")
    String imageUrl;

    @ManyToOne
    @JoinColumn(name = "`feedback_id`")
    Feedback feedback;
}
