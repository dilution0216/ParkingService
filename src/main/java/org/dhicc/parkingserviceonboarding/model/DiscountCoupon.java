package org.dhicc.parkingserviceonboarding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DiscountCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String couponCode; // 쿠폰 코드 ( "DISCOUNT10", "DISCOUNT50" 등)

    @Column(nullable = false)
    private int discountRate; // 할인율 (10프로 20프로 등등)
}