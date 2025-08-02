package com.unisew.server.models;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_type`")
    DesignItemType designItemType;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_category`")
    DesignItemCategory itemCategory;

    @OneToMany(mappedBy = "fabric", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignItem> designItems;

}
