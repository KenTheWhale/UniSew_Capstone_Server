package com.unisew.server.models;

import com.unisew.server.enums.ClothCategory;
import com.unisew.server.enums.ClothType;
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
    ClothType clothType;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_category`")
    ClothCategory clothCategory;

    @OneToMany(mappedBy = "fabric", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignRequest> designRequests;

}
