package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.CouponEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface CouponRepository extends CrudRepository<CouponEntity, String> {

    @Modifying
    @Query("UPDATE coupons SET used_count = used_count + 1 WHERE code = :code")
    void incrementUsedCount(String code);
}

