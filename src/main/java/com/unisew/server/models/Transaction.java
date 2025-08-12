package com.unisew.server.models;

import com.unisew.server.enums.PaymentType;
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
@Table(name = "`transaction`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`wallet_id`")
    Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "`receiver_id`")
    Customer receiver;

    @ManyToOne
    @JoinColumn(name = "`sender_id`")
    Customer sender;

    @Column(name = "`item_id`")
    Integer itemId;

    @Column(name = "`receiver_name`")
    String receiverName;

    @Column(name = "`sender_name`")
    String senderName;

    long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "`payment_type`")
    PaymentType paymentType;

    @Column(name = "`service_fee`")
    long serviceFee;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Column(name = "`payment_gateway_code`")
    String paymentGatewayCode;
}
