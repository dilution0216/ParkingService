package org.dhicc.parkingserviceonboarding.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.dhicc.parkingserviceonboarding.dto.UserUpdateRequest;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper; // JSON 직렬화/역직렬화를 위해 사용

    private String userToken;
    private String adminToken;
    private Long testUserId;
    private Long adminUserId;

    @BeforeEach
    void setUp() {
        System.out.println("🚀 BeforeEach 실행: 유저 생성 및 JWT 발급");

        userRepository.deleteAll();  // ✅ 기존 데이터 삭제 후 초기화

        // 일반 유저 생성
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("testPassword"));
        testUser.setRole(Role.ROLE_USER);

        User savedUser = userRepository.saveAndFlush(testUser);
        testUserId = savedUser.getId();

        // 관리자 유저 생성
        User adminUser = new User();
        adminUser.setUsername("adminUser");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("adminPassword"));
        adminUser.setRole(Role.ROLE_ADMIN);

        User savedAdmin = userRepository.saveAndFlush(adminUser);
        adminUserId = savedAdmin.getId();

        entityManager.clear();  // ✅ 영속성 컨텍스트 초기화 (쿼리 실행 시 DB에서 직접 가져오도록)

        // 🔥 JWT 토큰 생성
        userToken = "Bearer " + jwtProvider.generateToken(testUser.getUsername());
        adminToken = "Bearer " + jwtProvider.generateToken(adminUser.getUsername());
    }

    /** ✅ 로그인한 유저 정보 조회 */
    @Test
    void testGetCurrentUser() throws Exception {
        System.out.println("🔥 로그인한 유저 정보 조회 테스트 실행");

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    /** ✅ 특정 ID로 사용자 조회 */
    @Test
    void testGetUserById() throws Exception {
        System.out.println("🔥 특정 ID로 사용자 조회 테스트 실행");

        mockMvc.perform(get("/users/" + testUserId)
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    /** ❌ 존재하지 않는 ID로 조회 시 404 Not Found */
    @Test
    void testGetUserById_NotFound() throws Exception {
        System.out.println("🔥 존재하지 않는 사용자 조회 테스트 실행");

        mockMvc.perform(get("/users/99999")
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());
    }

    /** ✅ 로그인한 사용자 정보 업데이트 */
    @Test
    void testUpdateCurrentUser() throws Exception {
        System.out.println("🔥 로그인한 사용자 정보 업데이트 테스트 실행");

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setUsername("updatedUser");
        updateRequest.setEmail("updated@example.com");

        mockMvc.perform(patch("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    /** ❌ 잘못된 데이터로 사용자 업데이트 시 400 Bad Request */
    @Test
    void testUpdateCurrentUser_InvalidData() throws Exception {
        System.out.println("🔥 잘못된 데이터로 사용자 업데이트 테스트 실행");

        UserUpdateRequest invalidRequest = new UserUpdateRequest();
        invalidRequest.setUsername(""); // 빈 값

        mockMvc.perform(patch("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /** ✅ 관리자 계정으로 특정 유저 삭제 */
    @Test
    void testDeleteUser_Admin() throws Exception {
        System.out.println("🔥 관리자 계정으로 특정 유저 삭제 테스트 실행");

        mockMvc.perform(delete("/users/" + testUserId)
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNoContent());
    }

    /** ❌ 일반 유저가 다른 유저 삭제 시 403 Forbidden */
    @Test
    void testDeleteUser_ByNonAdmin() throws Exception {
        System.out.println("🔥 일반 유저가 다른 유저 삭제 시도 테스트 실행");

        mockMvc.perform(delete("/users/" + adminUserId)
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());
    }

    /** ❌ 존재하지 않는 유저 삭제 시 404 Not Found */
    @Test
    void testDeleteUser_NotFound() throws Exception {
        System.out.println("🔥 존재하지 않는 유저 삭제 시도 테스트 실행");

        mockMvc.perform(delete("/users/99999")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNotFound());
    }
}
