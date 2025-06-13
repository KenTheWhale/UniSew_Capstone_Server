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
@Table(name = "`cloth`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cloth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_type`")
    ClothType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "`cloth_category`")
    ClothCategory category;

    @Column(name = "`logo_image`")
    String logoImage;

    @Column(name = "`logo_position`")
    String logoPosition;

    @Column(name = "`logo_width`")
    int logoWidth;

    @Column(name = "`logo_height`")
    int logoHeight;

    String color;

    String fabric;

    String note;

    @OneToMany(mappedBy = "cloth")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "`request_id`")
    DesignRequest designRequest;

    @OneToMany(mappedBy = "cloth")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<SampleImage> sampleImages;

    @ManyToOne
    @JoinColumn(name = "template_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Cloth template;

    @OneToMany(mappedBy = "cloth")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<DesignDraft> designDrafts;
}
