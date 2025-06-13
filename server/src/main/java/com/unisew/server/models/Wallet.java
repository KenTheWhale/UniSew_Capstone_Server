package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`wallet`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {

    @Id
    @Column(name = "`account_id`")
    Integer id;

    long balance;

    @Column(name = "`pending_balance`")
    long pendingBalance;

    @Column(name = "`card_number`")
    String cardNumber;

    @Column(name = "`card_name`")
    String cardName;

    @Column(name = "`card_expired_date`")
    String cardExpiredDate;

    int cvv;

    @OneToOne
    @MapsId
    @JoinColumn(name = "`account_id`")
    Account account;

    @OneToMany(mappedBy = "wallet")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> transactions;
}
