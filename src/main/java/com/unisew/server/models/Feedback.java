package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
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

    @Column(name = "`school_content`")
    String schoolContent;

    @Column(name = "`partner_content`")
    String partnerContent;

    @Column(name = "`is_report`")
    boolean report;

    @Column(name = "`creation_date`")
    LocalDateTime creationDate;

    @Column(name = "`approval_date`")
    LocalDateTime approvalDate;

    @Column(name = "`message_for_school`")
    String messageForSchool;

    @Column(name = "`message_for_partner`")
    String messageForPartner;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`school_video_url`")
    String schoolVideoUrl;

    @Column(name = "`partner_video_url`")
    String partnerVideoUrl;

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
