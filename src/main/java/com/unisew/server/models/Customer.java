package com.unisew.server.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`customer`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
    @JoinColumn(name = "`account_id`")
    Account account;

    String address;

    @Column(name = "`tax_code`")
    String taxCode;

    String name;

    String phone;

    String avatar;

    @OneToOne(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Partner partner;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> receiverTransactions;

    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> senderTransactions;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SchoolDesign> schoolDesigns;

    @OneToMany(mappedBy = "school", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignRequest> designRequests;
}
