package org.dhicc.parkingserviceonboarding.controller;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.config.PricingPolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pricing-policy")
@RequiredArgsConstructor
public class PricingPolicyController {

    private final PricingPolicy pricingPolicy;

    // ✅ 모든 사용자 접근 가능 (권한 설정 제거)
    @GetMapping
    public ResponseEntity<PricingPolicy> getPricingPolicy() {
        return ResponseEntity.ok(pricingPolicy);
    }

    // ✅ 수정은 관리자 권한만 허용
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<Map<String, PricingPolicy>> updatePricingPolicy(@RequestBody PricingPolicy newPolicy) {
        // 🔥 변경 전 정책 저장
        PricingPolicy oldPolicy = new PricingPolicy();
        oldPolicy.setBaseFee(pricingPolicy.getBaseFee());
        oldPolicy.setExtraFeePer10Min(pricingPolicy.getExtraFeePer10Min());
        oldPolicy.setDailyMaxFee(pricingPolicy.getDailyMaxFee());
        oldPolicy.setMaxDaysCharged(pricingPolicy.getMaxDaysCharged());
        oldPolicy.setNightDiscount(pricingPolicy.getNightDiscount());
        oldPolicy.setWeekendDiscount(pricingPolicy.getWeekendDiscount());
        oldPolicy.setMaxCouponUses(pricingPolicy.getMaxCouponUses());
        oldPolicy.setMaxDiscountRate(pricingPolicy.getMaxDiscountRate());

        // 🔥 새로운 정책 적용
        pricingPolicy.setBaseFee(newPolicy.getBaseFee());
        pricingPolicy.setExtraFeePer10Min(newPolicy.getExtraFeePer10Min());
        pricingPolicy.setDailyMaxFee(newPolicy.getDailyMaxFee());
        pricingPolicy.setMaxDaysCharged(newPolicy.getMaxDaysCharged());
        pricingPolicy.setNightDiscount(newPolicy.getNightDiscount());
        pricingPolicy.setWeekendDiscount(newPolicy.getWeekendDiscount());
        pricingPolicy.setMaxCouponUses(newPolicy.getMaxCouponUses());
        pricingPolicy.setMaxDiscountRate(newPolicy.getMaxDiscountRate());

        // 🔥 응답에 이전 및 새로운 정책 포함
        Map<String, PricingPolicy> response = new HashMap<>();
        response.put("oldPolicy", oldPolicy);
        response.put("newPolicy", pricingPolicy);

        return ResponseEntity.ok(response);
    }
}
