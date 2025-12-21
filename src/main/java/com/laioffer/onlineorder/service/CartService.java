package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.CartEntity;
import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.OrderItemEntity;
import com.laioffer.onlineorder.model.CartDto;
import com.laioffer.onlineorder.model.OrderItemDto;
import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;


import java.util.*;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    public CartService(
            CartRepository cartRepository,
            MenuItemRepository menuItemRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void addMenuItemToCart(long customerId, long menuItemId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId).get();
        OrderItemEntity orderItem =
                orderItemRepository.findByCartIdAndMenuItemId(cart.id(), menuItem.id());

        Long orderItemId;
        int quantity;

        if (orderItem == null) {
            orderItemId = null;
            quantity = 1;
        } else {
            orderItemId = orderItem.id();
            quantity = orderItem.quantity() + 1;
        }

        OrderItemEntity newOrderItem = new OrderItemEntity(
                orderItemId,
                menuItemId,
                cart.id(),
                menuItem.price(),
                quantity
        );

        orderItemRepository.save(newOrderItem);
        cartRepository.updateTotalPrice(cart.id(), cart.totalPrice() + menuItem.price());
    }

    @Cacheable("cart")
    public CartDto getCart(Long customerId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        List<OrderItemEntity> orderItems = orderItemRepository.getAllByCartId(cart.id());
        List<OrderItemDto> orderItemDtos = getOrderItemDtos(orderItems);
        return new CartDto(cart, orderItemDtos);
    }

    @CacheEvict(cacheNames = "cart")
    @Transactional
    public void clearCart(Long customerId) {
        CartEntity cartEntity = cartRepository.getByCustomerId(customerId);
        orderItemRepository.deleteByCartId(cartEntity.id());
        cartRepository.updateTotalPrice(cartEntity.id(), 0.0);
    }

    private List<OrderItemDto> getOrderItemDtos(List<OrderItemEntity> orderItems) {
        Set<Long> menuItemIds = new HashSet<>();
        for (OrderItemEntity orderItem : orderItems) {
            menuItemIds.add(orderItem.menuItemId());
        }

        List<MenuItemEntity> menuItems = menuItemRepository.findAllById(menuItemIds);
        Map<Long, MenuItemEntity> menuItemMap = new HashMap<>();
        for (MenuItemEntity menuItem : menuItems) {
            menuItemMap.put(menuItem.id(), menuItem);
        }

        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (OrderItemEntity orderItem : orderItems) {
            MenuItemEntity menuItem = menuItemMap.get(orderItem.menuItemId());
            OrderItemDto orderItemDto = new OrderItemDto(orderItem, menuItem);
            orderItemDtos.add(orderItemDto);
        }
        return orderItemDtos;
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void removeMenuItemFromCart(long customerId, long orderItemId) {
        // 1. 获取购物车
        CartEntity cart = cartRepository.getByCustomerId(customerId);

        // 2. 查找要删除的条目
        OrderItemEntity item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        // 3. 计算新总价：原总价 - 条目总价
        // 既然 item.price() 已经是总价，直接相减
        double newTotalPrice = cart.totalPrice() - item.price();

        // 4. 执行删除 (使用 Repository 内置的 deleteById)
        orderItemRepository.deleteById(orderItemId);

        // 5. 更新购物车表中的总价
        cartRepository.updateTotalPrice(cart.id(), Math.max(0.0, newTotalPrice));
    }
}

