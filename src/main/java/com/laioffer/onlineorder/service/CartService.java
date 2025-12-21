package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.CartEntity;
import com.laioffer.onlineorder.entity.CouponEntity;
import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.OrderItemEntity;
import com.laioffer.onlineorder.model.CartDto;
import com.laioffer.onlineorder.model.CheckoutResultDto;
import com.laioffer.onlineorder.model.OrderItemDto;
import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.CouponRepository;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.OrderItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    private final CouponService couponService;
    private final CouponRepository couponRepository;

    public CartService(
            CartRepository cartRepository,
            MenuItemRepository menuItemRepository,
            OrderItemRepository orderItemRepository,
            CouponService couponService,
            CouponRepository couponRepository
    ) {
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.couponService = couponService;
        this.couponRepository = couponRepository;
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void addMenuItemToCart(long customerId, long menuItemId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId).orElseThrow();

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

        double newTotal = cart.totalPrice() + menuItem.price();
        cartRepository.updateTotalPrice(cart.id(), newTotal);

        // 如果已应用coupon，则同步刷新折扣
        refreshCouponIfPresent(cart, newTotal);
    }

    @Cacheable("cart")
    public CartDto getCart(Long customerId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        List<OrderItemEntity> orderItems = orderItemRepository.getAllByCartId(cart.id());
        List<OrderItemDto> orderItemDtos = getOrderItemDtos(orderItems);
        return new CartDto(cart, orderItemDtos);
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void clearCart(Long customerId) {
        CartEntity cartEntity = cartRepository.getByCustomerId(customerId);
        orderItemRepository.deleteByCartId(cartEntity.id());
        cartRepository.updateTotalPrice(cartEntity.id(), 0.0);
        cartRepository.clearCoupon(cartEntity.id());
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void removeMenuItemFromCart(long customerId, long orderItemId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);

        OrderItemEntity item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        double newTotalPrice = cart.totalPrice() - item.price();

        orderItemRepository.deleteById(orderItemId);

        newTotalPrice = Math.max(0.0, newTotalPrice);
        cartRepository.updateTotalPrice(cart.id(), newTotalPrice);

        refreshCouponIfPresent(cart, newTotalPrice);
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void applyCoupon(long customerId, String code) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        CouponEntity coupon = couponService.getValidCouponOrThrow(code);

        double cartTotal = round2(cart.totalPrice());
        double discount = couponService.computeDiscount(cartTotal, coupon);
        double finalTotal = round2(cartTotal - discount);

        cartRepository.updateCoupon(cart.id(), coupon.code(), discount, finalTotal);
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public CheckoutResultDto checkoutWithCoupon(long customerId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);

        double cartTotal = round2(cart.totalPrice());
        String appliedCoupon = null;
        double discount = 0.0;

        if (cart.couponCode() != null && !cart.couponCode().isBlank()) {
            CouponEntity coupon = couponService.getValidCouponOrThrow(cart.couponCode());
            discount = couponService.computeDiscount(cartTotal, coupon);
            appliedCoupon = coupon.code();

            if (discount > 0.0) {
                couponRepository.incrementUsedCount(appliedCoupon);
            }
        }

        double finalTotal = round2(cartTotal - discount);

        // clear cart + coupon fields
        orderItemRepository.deleteByCartId(cart.id());
        cartRepository.updateTotalPrice(cart.id(), 0.0);
        cartRepository.clearCoupon(cart.id());

        return new CheckoutResultDto(cartTotal, discount, finalTotal, appliedCoupon);
    }

    private void refreshCouponIfPresent(CartEntity cart, double newTotal) {
        if (cart.couponCode() == null || cart.couponCode().isBlank()) {
            return;
        }
        CouponEntity coupon = couponService.getValidCouponOrThrow(cart.couponCode());
        double cartTotal = round2(newTotal);
        double discount = couponService.computeDiscount(cartTotal, coupon);
        double finalTotal = round2(cartTotal - discount);
        cartRepository.updateCoupon(cart.id(), coupon.code(), discount, finalTotal);
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
            orderItemDtos.add(new OrderItemDto(orderItem, menuItem));
        }
        return orderItemDtos;
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
