package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
