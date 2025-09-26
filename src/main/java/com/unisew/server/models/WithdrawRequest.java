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

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`withdraw_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WithdrawRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "`wallet_id`")
    Wallet wallet;

    @Column(name = "`creation_date`")
    LocalDateTime creationDate;

    @Column(name = "`withdraw_amount`")
    long withdrawAmount;

    @Column(name = "`evidence_image_url`")
    String evidenceImageUrl;

    @Enumerated(EnumType.STRING)
    Status status;

}
