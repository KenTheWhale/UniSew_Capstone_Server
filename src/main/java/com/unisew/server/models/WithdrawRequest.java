package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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

    LocalDate creationDate;

    long withdrawAmount;

    Status status;

    @ManyToOne
    @JoinColumn(name = "wallet_id)")
    Wallet wallet;

}
