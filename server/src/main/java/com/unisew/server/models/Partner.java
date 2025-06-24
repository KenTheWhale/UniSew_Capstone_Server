package com.unisew.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
@Table(name = "`partner`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String street;

    String ward;

    String district;

    String province;

    @Column(name = "`busy`")
    boolean isBusy;

    @OneToOne
    @JoinColumn(name = "`profile_id`")
    Profile profile;
}
