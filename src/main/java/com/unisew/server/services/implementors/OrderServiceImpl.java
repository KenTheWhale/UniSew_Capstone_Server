package com.unisew.server.services.implementors;

import com.unisew.server.enums.DesignItemSize;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Order;
import com.unisew.server.models.OrderDetail;
import com.unisew.server.models.Partner;
import com.unisew.server.models.SchoolDesign;
import com.unisew.server.repositories.OrderDetailRepo;
import com.unisew.server.repositories.OrderRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.SchoolDesignRepo;
import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.OrderService;
import com.unisew.server.utils.ResponseBuilder;
import com.unisew.server.validations.OrderValidation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepo orderRepo;

    PartnerRepo partnerRepo;

    SchoolDesignRepo schoolDesignRepo;

    OrderDetailRepo orderDetailRepo;

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request) {
        String error = OrderValidation.validate(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.OK, error, null);
        }

        SchoolDesign schoolDesign = schoolDesignRepo.findById(request.getSchoolDesignId())
                .orElse(null);
        if (schoolDesign == null) {
            return ResponseBuilder.build(HttpStatus.OK, "School Design not found", null);
        }

        Partner garment = partnerRepo.findById(request.getGarmentId())
                .orElse(null);
        if (garment == null) {
            return ResponseBuilder.build(HttpStatus.OK, "Garment not found", null);
        }

        Order order = Order.builder()
                .schoolDesign(schoolDesign)
                .garmentId(garment.getId())
                .garmentName(garment.getCustomer().getName())
                .deadline(request.getDeadline())
                .price(0)
                .serviceFee(0)
                .orderDate(LocalDate.now())
                .note(request.getNote())
                .status(Status.ORDER_PENDING)
                .build();

        order = orderRepo.save(order);

        List<OrderDetail> orderDetailEntities = new ArrayList<>();
        if (request.getOrderDetails() != null) {
            for (CreateOrderRequest.OrderItem item : request.getOrderDetails()) {
                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .deliveryItemId(item.getDeliveryItemId())
                        .size(DesignItemSize.valueOf(item.getSize()))
                        .quantity(item.getQuantity())
                        .build();
                orderDetailEntities.add(detail);
            }
            orderDetailRepo.saveAll(orderDetailEntities);
        }

        order.setOrderDetails(orderDetailEntities);

        return ResponseBuilder.build(HttpStatus.OK, "Order created successfully!", null);
    }
}
