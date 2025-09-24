package com.unisew.server.models;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`fabric`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fabric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    String description;

    @Column(name = "`for_shirt`")
    boolean forShirt;

    @Column(name = "`for_pants`")
    boolean forPants;

    @Column(name = "`for_skirt`")
    boolean forSkirt;

    @Column(name = "`for_regular`")
    boolean forRegular;

    @Column(name = "`for_pe`")
    boolean forPE;

    @Column(columnDefinition = "jsonb", name = "`garment_price`")
    @Type(JsonBinaryType.class)
    Object garmentPrice;

    @OneToMany(mappedBy = "fabric", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignItem> designItems;

}
