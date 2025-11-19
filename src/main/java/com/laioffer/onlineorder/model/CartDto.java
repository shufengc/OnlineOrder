package com.laioffer.onlineorder.model;

import java.util.List;

import com.laioffer.onlineorder.entity.CartEntity;

public record CartDto(
        Long id,
        Double totalPrice,
        List<OrderItemDto> orderItems
) {
    public CartDto(CartEntity entity, List<OrderItemDto> orderItems) {
        this(entity.id(), entity.totalPrice(), orderItems);
    }
}

