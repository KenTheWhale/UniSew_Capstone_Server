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
@Table(name = "`request_receipt`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;

    @ManyToOne
    @JoinColumn(name = "`package_id`")
    Packages packages;

    @Column(name = "`acceptance_deadline`")
    LocalDate acceptanceDeadline;

    @Enumerated(EnumType.STRING)
    Status status;
}
