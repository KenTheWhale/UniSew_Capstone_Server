package com.unisew.server.models;

import com.unisew.server.enums.ClothSize;
import com.unisew.server.enums.ClothType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "`defective_order_detail`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefectiveOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_type`")
    ClothType clothType;

    @Enumerated(EnumType.STRING)
    ClothSize size;

    int quantity;

    @ManyToOne
    @JoinColumn(name = "`defective_order_id`")
    DefectiveOrder defectiveOrder;
}
