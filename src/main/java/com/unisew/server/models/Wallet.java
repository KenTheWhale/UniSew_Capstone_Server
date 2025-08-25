package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
    @JoinColumn(name = "`account_id`")
    Account account;

    long balance;

    @Column(name = "`pending_balance`")
    long pendingBalance;

    @Column(name = "`card_owner`")
    String cardOwner;

    @Column(name = "`bank`")
    String bank;

    @Column(name = "`bank_account_number`")
    String bankAccountNumber;

    @OneToMany(mappedBy = "wallet")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> transactions;

    @OneToMany(mappedBy = "wallet")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<WithdrawRequest> withdrawRequests;
}
