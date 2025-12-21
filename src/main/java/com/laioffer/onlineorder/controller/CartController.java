package com.laioffer.onlineorder.controller;

import com.laioffer.onlineorder.entity.CustomerEntity;
import com.laioffer.onlineorder.model.AddToCartBody;
import com.laioffer.onlineorder.model.ApplyCouponBody;
import com.laioffer.onlineorder.model.CartDto;
import com.laioffer.onlineorder.model.CheckoutResultDto;
import com.laioffer.onlineorder.service.CartService;
import com.laioffer.onlineorder.service.CustomerService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {

    private final CartService cartService;
    private final CustomerService customerService;

    public CartController(CartService cartService, CustomerService customerService) {
        this.cartService = cartService;
        this.customerService = customerService;
    }

    @GetMapping("/cart")
    public CartDto getCart(@AuthenticationPrincipal User user) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        return cartService.getCart(customer.id());
    }

    @PostMapping("/cart")
    public void addToCart(@AuthenticationPrincipal User user, @RequestBody AddToCartBody body) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        cartService.addMenuItemToCart(customer.id(), body.menuId());
    }

    @DeleteMapping("/cart/items/{orderItemId}")
    public void removeMenuItemFromCart(@AuthenticationPrincipal User user, @PathVariable long orderItemId) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        cartService.removeMenuItemFromCart(customer.id(), orderItemId);
    }

    @PostMapping("/cart/apply-coupon")
    public void applyCoupon(@AuthenticationPrincipal User user, @RequestBody ApplyCouponBody body) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        cartService.applyCoupon(customer.id(), body.code());
    }

    @PostMapping("/cart/checkout-with-coupon")
    public CheckoutResultDto checkoutWithCoupon(@AuthenticationPrincipal User user) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        return cartService.checkoutWithCoupon(customer.id());
    }

    @PostMapping("/cart/checkout")
    public void checkout(@AuthenticationPrincipal User user) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        cartService.clearCart(customer.id());
    }
}
