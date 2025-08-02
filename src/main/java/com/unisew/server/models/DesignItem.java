package com.unisew.server.models;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
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

    @ManyToOne
    @JoinColumn(name = "`fabric_id`")
    Fabric fabric;
}
