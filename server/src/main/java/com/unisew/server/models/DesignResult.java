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
@Table(name = "`design_result`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String description;

    @Column(name = "`design_date`")
    LocalDate designDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @ManyToOne
    @JoinColumn(name = "`item_id`")
    DesignRequestItem designRequestItem;

    @OneToMany(mappedBy = "designResult")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ResultImage> resultImages;

    @OneToMany(mappedBy = "designResult")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ReDesignRequest> reDesignRequests;
}
