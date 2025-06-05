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
@Table(name = "`defective_order`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefectiveOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String issue;

    LocalDate deadline;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "`completion_date`")
    LocalDate completionDate;

    @ManyToOne
    @JoinColumn(name = "`sub_order_id`")
    SubOrder subOrder;

    @OneToMany(mappedBy = "defectiveOrder")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DefectiveOrderDetail> defectiveOrderDetails;
}
