package com.unisew.server.models;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import jakarta.persistence.*;
import lombok.*;
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
