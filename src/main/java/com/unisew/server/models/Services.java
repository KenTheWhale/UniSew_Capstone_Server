package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`services`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String rule;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToMany(mappedBy = "service")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<PackageService> packageServices;
}
