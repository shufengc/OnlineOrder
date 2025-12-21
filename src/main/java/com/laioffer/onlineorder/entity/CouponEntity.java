package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("coupons")
public record CouponEntity(
        @Id String code,
        String type,          // PERCENT_OFF | AMOUNT_OFF | THRESHOLD_OFF
        Double value,
        LocalDateTime expiry,
        Double minSpend,
        Integer usageLimit,
        Integer usedCount
) {}
