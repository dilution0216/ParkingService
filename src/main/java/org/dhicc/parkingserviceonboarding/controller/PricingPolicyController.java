package org.dhicc.parkingserviceonboarding.controller;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.config.PricingPolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing-policy")
@RequiredArgsConstructor
public class PricingPolicyController {

    private final PricingPolicy pricingPolicy;

    // 현재 요금 정책 조회 API
    @GetMapping
    public ResponseEntity<PricingPolicy> getPricingPolicy() {
        return ResponseEntity.ok(pricingPolicy);
    }

    // 요금 정책 변경 API
    @PutMapping
    public ResponseEntity<String> updatePricingPolicy(@RequestBody PricingPolicy newPolicy) {
        // 변경 전 정책 저장
        PricingPolicy oldPolicy = new PricingPolicy();
        oldPolicy.setBaseFee(pricingPolicy.getBaseFee());
        oldPolicy.setExtraFeePer10Min(pricingPolicy.getExtraFeePer10Min());
        oldPolicy.setDailyMaxFee(pricingPolicy.getDailyMaxFee());
        oldPolicy.setMaxDaysCharged(pricingPolicy.getMaxDaysCharged());
        oldPolicy.setNightDiscount(pricingPolicy.getNightDiscount());
        oldPolicy.setWeekendDiscount(pricingPolicy.getWeekendDiscount());
        oldPolicy.setMaxCouponUses(pricingPolicy.getMaxCouponUses());
        oldPolicy.setMaxDiscountRate(pricingPolicy.getMaxDiscountRate());

        // 새로운 정책 적용
        pricingPolicy.setBaseFee(newPolicy.getBaseFee());
        pricingPolicy.setExtraFeePer10Min(newPolicy.getExtraFeePer10Min());
        pricingPolicy.setDailyMaxFee(newPolicy.getDailyMaxFee());
        pricingPolicy.setMaxDaysCharged(newPolicy.getMaxDaysCharged());
        pricingPolicy.setNightDiscount(newPolicy.getNightDiscount());
        pricingPolicy.setWeekendDiscount(newPolicy.getWeekendDiscount());
        pricingPolicy.setMaxCouponUses(newPolicy.getMaxCouponUses());
        pricingPolicy.setMaxDiscountRate(newPolicy.getMaxDiscountRate());

        // 응답 메시지 생성
        String responseMessage = "요금 정책이 성공적으로 변경되었습니다.\n" +
                "이전 정책: " + oldPolicy + "\n" +
                "변경 후 정책: " + pricingPolicy;

        return ResponseEntity.ok(responseMessage);
    }
}