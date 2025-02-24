package org.dhicc.parkingserviceonboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.dhicc.parkingserviceonboarding.dto.SubscriptionDTO;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.dhicc.parkingserviceonboarding.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager; // ✅ 추가

    @Autowired
    private JwtProvider jwtProvider; // ✅ 추가

    @Autowired
    private ObjectMapper objectMapper; // ✅ JSON 변환을 위한 ObjectMapper 추가

    private String userToken;
    private String adminToken;
    private Long testUserId;
    private Long adminUserId;

    @BeforeEach
    void setUp() {
        System.out.println("🚀 BeforeEach 실행: 유저 생성 및 JWT 발급");

        subscriptionRepository.deleteAll(); // ✅ 기존 정기권 데이터 초기화
        userRepository.deleteAll(); // ✅ 기존 유저 데이터 초기화

        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("testPassword"));
        testUser.setRole(Role.ROLE_USER);
        testUserId = userRepository.saveAndFlush(testUser).getId();

        User adminUser = new User();
        adminUser.setUsername("adminUser");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("adminPassword"));
        adminUser.setRole(Role.ROLE_ADMIN);
        adminUserId = userRepository.saveAndFlush(adminUser).getId();

        entityManager.clear();

        userToken = "Bearer " + jwtProvider.generateToken(testUser.getUsername());
        adminToken = "Bearer " + jwtProvider.generateToken(adminUser.getUsername());

        System.out.println("✅ userToken: " + userToken);
        System.out.println("✅ adminToken: " + adminToken);
    }

    /** ✅ 1. 로그인한 사용자의 정기권 조회 테스트 */
    @Test
    void testGetMySubscription() throws Exception {
        System.out.println("🔥 로그인한 사용자의 정기권 조회 테스트 실행");

        Subscription subscription = new Subscription();
        subscription.setVehicleNumber("ABC123");
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setUser(userRepository.findById(testUserId).orElseThrow());
        subscriptionRepository.saveAndFlush(subscription);

        mockMvc.perform(get("/subscription/me")
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleNumber").value("ABC123"));
    }


    /** ✅ 2. 정기권 등록 테스트 (ROLE_USER만 가능) */
    @Test
    void testRegisterSubscription() throws Exception {
        System.out.println("🔥 정기권 등록 테스트 실행");

        subscriptionRepository.deleteAll(); // ✅ 중복 데이터 방지

        SubscriptionDTO request = new SubscriptionDTO("XYZ999", LocalDate.now(), LocalDate.now().plusMonths(1), testUserId);

        mockMvc.perform(post("/subscription/register")
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleNumber").value("XYZ999"));
    }


    /** ✅ 3. 정기권 취소 테스트 (ROLE_ADMIN만 가능) */
    @Test
    void testCancelSubscription_AdminOnly() throws Exception {
        System.out.println("🔥 관리자 계정으로 정기권 취소 테스트 실행");

        Subscription subscription = new Subscription();
        subscription.setVehicleNumber("DELETE123");
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setUser(userRepository.findById(testUserId).orElseThrow());
        subscriptionRepository.saveAndFlush(subscription);

        mockMvc.perform(delete("/subscription/admin/DELETE123")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk());
    }

    /** ✅ 4. 일반 유저가 정기권 취소 시도할 경우 403 Forbidden */
    @Test
    void testCancelSubscription_UserForbidden() throws Exception {
        System.out.println("🔥 일반 유저가 정기권 취소 시도 테스트 실행");

        // ✅ 삭제 대상 정기권 사전 등록 (중요)
        Subscription subscription = new Subscription();
        subscription.setVehicleNumber("DELETE123");
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setUser(userRepository.findById(testUserId).orElseThrow());
        subscriptionRepository.saveAndFlush(subscription);

        mockMvc.perform(delete("/subscription/admin/DELETE123")
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden()); // ✅ 403이 정상 반환되어야 함
    }
}