package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.CouponEntity;
import com.laioffer.onlineorder.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponEntity getValidCouponOrThrow(String code) {
        CouponEntity coupon = couponRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code"));

        // expiry check
        if (coupon.expiry() != null && coupon.expiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Coupon expired");
        }

        // usage limit check: null or <= 0 means unlimited
        Integer limit = coupon.usageLimit();
        Integer used = coupon.usedCount() == null ? 0 : coupon.usedCount();
        if (limit != null && limit > 0 && used >= limit) {
            throw new IllegalArgumentException("Coupon usage limit reached");
        }

        return coupon;
    }

    public double computeDiscount(double cartTotal, CouponEntity coupon) {
        double minSpend = coupon.minSpend() == null ? 0.0 : coupon.minSpend();
        if (cartTotal < minSpend) {
            return 0.0;
        }

        double discount;
        switch (coupon.type()) {
            case "PERCENT_OFF" -> discount = cartTotal * (coupon.value() / 100.0);
            case "AMOUNT_OFF" -> discount = coupon.value();
            case "THRESHOLD_OFF" -> discount = coupon.value();
            default -> throw new IllegalArgumentException("Unsupported coupon type");
        }

        // clamp
        if (discount < 0) discount = 0;
        if (discount > cartTotal) discount = cartTotal;

        // money rounding (2 decimals)
        return round2(discount);

    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

}

