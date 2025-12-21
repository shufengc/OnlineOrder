package com.laioffer.onlineorder.model;

public record CheckoutResultDto(
        Double cartTotal,
        Double discount,
        Double finalTotal,
        String appliedCoupon
) {}

