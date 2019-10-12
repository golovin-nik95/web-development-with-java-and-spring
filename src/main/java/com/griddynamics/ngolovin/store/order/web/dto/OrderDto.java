package com.griddynamics.ngolovin.store.order.web.dto;

import com.griddynamics.ngolovin.store.order.domain.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDto {

    private Long id;
    private LocalDate date;
    private BigDecimal total;
    private OrderStatus status;
    private List<OrderItemDto> items;
}
