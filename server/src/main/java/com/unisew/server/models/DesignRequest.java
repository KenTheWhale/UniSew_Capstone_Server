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

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`design_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`creation_date`")
    LocalDate creationDate;

    @Column(name = "`private`")
    boolean isPrivate;

    @Enumerated(EnumType.STRING)
    Status status;

    @ManyToOne
    @JoinColumn(name = "`school_id`")
    Account school;

    @ManyToOne
    @JoinColumn(name = "`designer_id`")
    Account designer;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Cloth> cloths;

    @OneToOne
    @JoinColumn(name = "`feedback_id`")
    Feedback feedback;

    @OneToMany(mappedBy = "designRequest")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "`package_id`")
    Package pkg;
}
