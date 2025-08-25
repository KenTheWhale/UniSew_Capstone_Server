package com.unisew.server.models;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`account_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String email;

    @Enumerated(EnumType.STRING)
    Role role;

    String address;

    @Column(name = "`tax_code`")
    String taxCode;

    String phone;

    @Enumerated(EnumType.STRING)
    Status status;
}
