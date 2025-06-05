package com.unisew.server.models;

import jakarta.persistence.Entity;
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

    @OneToMany(mappedBy = "fabric")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DeclarationItem> declarationItems;

    @OneToMany(mappedBy = "fabric")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignRequestItem> designRequestItems;
}
