package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "`package`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @Column(name = "`headerContent`")
    String headerContent;

    @Column(name = "`deliveryDuration`")
    int deliveryDuration;

    @Column(name = "`revisionTime`")
    int revisionTime;

    long fee;

    @Enumerated(EnumType.STRING)
    Status status;

    @ManyToOne
    @JoinColumn(name = "`designer_id`")
    Designer designer;

    @OneToMany(mappedBy = "pkg")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<PackageRule> rules;
}
