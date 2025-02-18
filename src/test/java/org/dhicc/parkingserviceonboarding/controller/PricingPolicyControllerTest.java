package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.config.PricingPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PricingPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PricingPolicy pricingPolicy;

    @BeforeEach
    void resetPricingPolicy() {
        pricingPolicy.setBaseFee(1000);
        pricingPolicy.setExtraFeePer10Min(500);
        pricingPolicy.setDailyMaxFee(15000);
        pricingPolicy.setMaxDaysCharged(3);
        pricingPolicy.setNightDiscount(0.2);
        pricingPolicy.setWeekendDiscount(0.1);
    }

    /** 1. 현재 요금 정책 조회 테스트 */
    @Test
    void testGetPricingPolicy() throws Exception {
        mockMvc.perform(get("/pricing-policy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseFee", is(1000)))
                .andExpect(jsonPath("$.extraFeePer10Min", is(500)))
                .andExpect(jsonPath("$.dailyMaxFee", is(15000)))
                .andExpect(jsonPath("$.maxDaysCharged", is(3)))
                .andExpect(jsonPath("$.nightDiscount", is(0.2)))
                .andExpect(jsonPath("$.weekendDiscount", is(0.1)));
    }

    /** 2. 요금 정책 변경 테스트 */
    @Test
    void testUpdatePricingPolicy() throws Exception {
        String newPolicyJson = """
            {
                "baseFee": 1200,
                "extraFeePer10Min": 600,
                "dailyMaxFee": 18000,
                "maxDaysCharged": 5,
                "nightDiscount": 0.25,
                "weekendDiscount": 0.15
            }
        """;

        mockMvc.perform(put("/pricing-policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPolicyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oldPolicy.baseFee", is(1000)))
                .andExpect(jsonPath("$.newPolicy.baseFee", is(1200)))
                .andExpect(jsonPath("$.oldPolicy.extraFeePer10Min", is(500)))
                .andExpect(jsonPath("$.newPolicy.extraFeePer10Min", is(600)))
                .andExpect(jsonPath("$.oldPolicy.dailyMaxFee", is(15000)))
                .andExpect(jsonPath("$.newPolicy.dailyMaxFee", is(18000)))
                .andExpect(jsonPath("$.oldPolicy.nightDiscount", is(0.2)))
                .andExpect(jsonPath("$.newPolicy.nightDiscount", is(0.25)))
                .andExpect(jsonPath("$.oldPolicy.weekendDiscount", is(0.1)))
                .andExpect(jsonPath("$.newPolicy.weekendDiscount", is(0.15)));

        // 변경된 정책이 반영되었는지 확인
        mockMvc.perform(get("/pricing-policy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseFee", is(1200)))
                .andExpect(jsonPath("$.extraFeePer10Min", is(600)))
                .andExpect(jsonPath("$.dailyMaxFee", is(18000)))
                .andExpect(jsonPath("$.maxDaysCharged", is(5)))
                .andExpect(jsonPath("$.nightDiscount", is(0.25)))
                .andExpect(jsonPath("$.weekendDiscount", is(0.15)));
    }
}

