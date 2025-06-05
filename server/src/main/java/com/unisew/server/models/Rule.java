package com.unisew.server.models;

import com.unisew.server.enums.RuleSubject;
import com.unisew.server.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "`rule`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    String description;

    @Enumerated(EnumType.STRING)
    RuleSubject subject;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToMany(mappedBy = "rule")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ContractRule> contractRules;
}
