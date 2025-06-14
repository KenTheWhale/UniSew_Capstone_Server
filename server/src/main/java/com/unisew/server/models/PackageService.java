package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`package_service`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackageService {

    @EmbeddedId
    ID id;

    @ManyToOne
    @MapsId("packageId")
    @JoinColumn(name = "`package_id`")
    Package pkg;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "`service_id`")
    Services service;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ID {

        @Column(name = "`package_id`")
        int packageId;

        @Column(name = "`service_id`")
        int serviceId;
    }
}
