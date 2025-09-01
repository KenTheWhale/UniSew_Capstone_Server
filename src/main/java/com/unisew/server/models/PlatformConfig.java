package com.unisew.server.models;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`platform_config`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlatformConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String key;

    @Column(columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    Object value;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Column(name = "`modified_date`")
    LocalDate modifiedDate;

}
