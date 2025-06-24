package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`thumbnail_img`")
    String thumbnailImg;

    @Column(name = "`short_preview`")
    String shortPreview;

    String bio;

    @OneToOne
    @JoinColumn(name = "`profile_id`")
    Profile profile;

    @OneToMany(mappedBy = "designer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Packages> packages;
}
