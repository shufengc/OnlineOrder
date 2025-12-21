package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.CartEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<CartEntity, Long> {

    CartEntity getByCustomerId(Long customerId);

    @Modifying
    @Query("UPDATE carts SET total_price = :totalPrice WHERE id = :cartId")
    void updateTotalPrice(Long cartId, Double totalPrice);

    @Modifying
    @Query("""
           UPDATE carts
           SET coupon_code = :code,
               discount = :discount,
               final_total = :finalTotal
           WHERE id = :cartId
           """)
    void updateCoupon(Long cartId, String code, Double discount, Double finalTotal);

    @Modifying
    @Query("""
           UPDATE carts
           SET coupon_code = NULL,
               discount = NULL,
               final_total = NULL
           WHERE id = :cartId
           """)
    void clearCoupon(Long cartId);
}
