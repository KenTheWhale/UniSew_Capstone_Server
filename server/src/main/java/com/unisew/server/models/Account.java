package com.unisew.server.models;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`account`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String email;

    @Enumerated(EnumType.STRING)
    Role role;

    @Column(name = "`register_date`")
    LocalDate registerDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToOne(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Profile profile;

    @OneToOne(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Wallet wallet;

    @OneToMany(mappedBy = "school", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignRequest> schoolDesignRequests;

    @OneToMany(mappedBy = "school")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Order> schoolOrders;

    @OneToMany(mappedBy = "garment")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Order> garmentOrders;

    @OneToMany(mappedBy = "sender")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> senderTransactions;

    @OneToMany(mappedBy = "receiver")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> receiverTransactions;



}
