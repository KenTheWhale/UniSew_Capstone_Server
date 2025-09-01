package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`appeals`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Appeals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "feedback_id")
    Feedback feedback;

    @Column(name = "`account_id`")
    int accountId;

    @Column(name = "`reason`")
    String reason;

    @Column(name = "`admin_response`")
    String adminResponse;

    @Column(name = "`video_url`")
    String videoUrl;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Column(name = "`approval_date`")
    LocalDate approvalDate;

    @Enumerated(EnumType.STRING)
    Status status;
}
