package com.unisew.server.models;

import com.unisew.server.enums.ClothCategory;
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

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`design_request_item`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_type`")
    ClothType clothType;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_category`")
    ClothCategory clothCategory;

    String color;

    @Column(name = "`logo_image`")
    String logoImage;

    @Column(name = "`private_design`")
    boolean privateDesign;

    String description;

    @OneToMany(mappedBy = "designRequestItem")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "`fabric_id`")
    Fabric fabric;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;

    @OneToMany(mappedBy = "designRequestItem")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<RequestedImage> requestedImages;

    @ManyToOne
    @JoinColumn(name = "template_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    DesignRequestItem template;

    @OneToMany(mappedBy = "designRequestItem")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignResult> designResults;
}
