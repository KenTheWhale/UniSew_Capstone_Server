package com.unisew.server.models;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`design_item`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;

    @ManyToOne
    @JoinColumn(name = "`fabric_id`")
    Fabric fabric;

    @Enumerated(EnumType.STRING)
    DesignItemType type;

    @Enumerated(EnumType.STRING)
    DesignItemCategory category;

    @Column(name = "`logo_position`")
    String logoPosition;

    String color;

    String note;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @OneToMany(mappedBy = "designItem")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SampleImage> sampleImages;
}
