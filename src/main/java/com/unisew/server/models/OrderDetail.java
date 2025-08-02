package com.unisew.server.models;

import com.unisew.server.enums.DesignItemSize;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "`order_detail`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "`order_id`")
    Order order;

    @Column(name = "`delivery_item_id`")
    int deliveryItemId;

    @Enumerated(EnumType.STRING)
    DesignItemSize size;

    int quantity;

}
