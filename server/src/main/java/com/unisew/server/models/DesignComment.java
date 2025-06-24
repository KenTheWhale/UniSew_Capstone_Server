package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`design_comment`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`sender_id`")
    Integer senderId;

    @Column(name = "`sender_role`")
    String senderRole;

    String content;

    @Column(name = "`creation_date`")
    LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;
}
