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
@Table(name = "`feedback`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int rating;

    String content;

    @Column(name = "`is_report`")
    boolean report;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @OneToOne(mappedBy = "feedback", fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    DesignRequest designRequest;

    @OneToOne(mappedBy = "feedback", fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Order order;

    @OneToMany(mappedBy = "feedback")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<FeedbackImage> feedbackImages;
}
