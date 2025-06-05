package com.unisew.server.models;

import com.unisew.server.enums.ClothCategory;
import com.unisew.server.enums.ClothType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "`declaration_item`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeclarationItem {

    @EmbeddedId
    ID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_type`")
    ClothType clothType;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_category`")
    ClothCategory clothCategory;

    long price;

    @ManyToOne
    @MapsId("declarationId")
    @JoinColumn(name = "`declaration_id`")
    GarmentDeclaration garmentDeclaration;

    @ManyToOne
    @MapsId("fabricId")
    @JoinColumn(name = "`fabric_id`")
    Fabric fabric;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ID implements Serializable {
        @Column(name = "`declaration_id`")
        Integer declarationId;

        @Column(name = "`fabric_id`")
        Integer fabricId;
    }
}
