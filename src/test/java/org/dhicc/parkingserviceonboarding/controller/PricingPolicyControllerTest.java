package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.config.PricingPolicy;
import org.dhicc.parkingserviceonboarding.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        // ✅ JWT 토큰 생성
        adminToken = "Bearer " + jwtProvider.generateToken("adminUser", "ROLE_ADMIN");
        userToken = "Bearer " + jwtProvider.generateToken("testUser", "ROLE_USER");
    }

    /** ✅ 1. 현재 요금 정책 조회 테스트 (모든 사용자 접근 가능) */
    @Test
    void testGetPricingPolicy() throws Exception {
        mockMvc.perform(get("/pricing-policy"))
                .andExpect(status().isOk()) // ✅ 인증 필요 없음
                .andExpect(jsonPath("$.baseFee", is(1000)))
                .andExpect(jsonPath("$.extraFeePer10Min", is(500)))
                .andExpect(jsonPath("$.dailyMaxFee", is(15000)))
                .andExpect(jsonPath("$.maxDaysCharged", is(3)))
                .andExpect(jsonPath("$.nightDiscount", is(0.2)))
                .andExpect(jsonPath("$.weekendDiscount", is(0.1)));
    }

    /** ✅ 2. 요금 정책 변경 테스트 (관리자 권한) */
    @Test
    void testUpdatePricingPolicy_AsAdmin() throws Exception {
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
                        .header(HttpHeaders.AUTHORIZATION, adminToken) // ✅ 관리자 권한 필요
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPolicyJson))
                .andExpect(status().isOk());
    }

    /** ✅ 3. 권한 없는 유저가 요금 정책 변경 시도 → 403 발생 확인 */
    @Test
    void testUpdatePricingPolicy_AsUser_Fail() throws Exception {
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
                        .header(HttpHeaders.AUTHORIZATION, userToken) // ✅ 권한 없는 사용자
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPolicyJson))
                .andExpect(status().isForbidden()); // ✅ 403 발생 확인
    }
}
