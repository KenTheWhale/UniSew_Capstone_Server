package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`contract_rule`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractRule {

    @EmbeddedId
    ID id;

    @ManyToOne
    @MapsId("contractId")
    @JoinColumn(name = "`contract_id`")
    Contract contract;

    @ManyToOne
    @MapsId("ruleId")
    @JoinColumn(name = "`rule_id`")
    Rule rule;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ID implements Serializable {

        @Column(name = "`contract_id`")
        Integer contractId;

        @Column(name = "`rule_id`")
        Integer ruleId;
    }
}
