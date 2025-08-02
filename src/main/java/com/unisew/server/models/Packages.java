package com.unisew.server.models;

import com.unisew.server.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`package`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Packages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`designer_id`")
    Partner designer;

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

    @OneToMany(mappedBy = "pkg")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<PackageService> packageServices;

    @OneToMany(mappedBy = "pkg")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<RequestReceipt> requestReceipts;
}
