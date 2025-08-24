package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`deactivate_ticket`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeactivateTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String reason;

    LocalDate startDate;

    LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "`accountId`")
    Account account;
}
