package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "`designer`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Designer {

    @Id
    @Column(name = "`profile_id`")
    Integer id;

    @Column(name = "`short_preview`")
    String shortPreview;

    String bio;

    @OneToOne
    @MapsId
    @JoinColumn(name = "`profile_id`")
    Profile profile;

    @OneToMany(mappedBy = "designer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Package> packages;
}
