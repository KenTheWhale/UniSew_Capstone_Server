package com.unisew.server.models;

import com.unisew.server.enums.PaymentType;
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
@Table(name = "`transaction`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`sender_name`")
    String senderName;

    @Column(name = "`receiver_name`")
    String receiverName;

    long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "`payment_type`")
    PaymentType paymentType;

    String note;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`payment_gateway_code`")
    String paymentGatewayCode;

    @Column(name = "`payment_gateway_message`")
    String paymentGatewayMessage;

    @ManyToOne
    @JoinColumn(name = "`order_id`")
    Order order;

    @ManyToOne
    @JoinColumn(name = "`design_request_id`")
    DesignRequest designRequest;

    @ManyToOne
    @JoinColumn(name = "`sender_id`")
    Account sender;

    @ManyToOne
    @JoinColumn(name = "`receiver_id`")
    Account receiver;

    @ManyToOne
    @JoinColumn(name = "`wallet_id`")
    Wallet wallet;
}
