package com.laioffer.onlineorder.model;

import java.util.List;

import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.RestaurantEntity;

public record RestaurantDto(
        Long id,
        String name,
        String address,
        String phone,
        String imageUrl,
        List<MenuItemDto> menuItems
) {
    public RestaurantDto(RestaurantEntity entity, List<MenuItemDto> menuItems) {
        this(
                entity.id(),
                entity.name(),
                entity.address(),
                entity.phone(),
                entity.imageUrl(),
                menuItems
        );
    }
}

