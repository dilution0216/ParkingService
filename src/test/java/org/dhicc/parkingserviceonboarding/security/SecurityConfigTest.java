package org.dhicc.parkingserviceonboarding.security;

import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupUsers() {
        userRepository.deleteAll(); // 기존 데이터 삭제

        User adminUser = new User();
        adminUser.setUsername("admin1");
        adminUser.setEmail("admin1@example.com"); // ✅ email 추가
        adminUser.setPassword(passwordEncoder.encode("password")); // 비밀번호 암호화
        adminUser.setRole(Role.ROLE_ADMIN);
        userRepository.saveAndFlush(adminUser); // DB에 반영

        User normalUser = new User();
        normalUser.setUsername("user1");
        normalUser.setEmail("user1@example.com"); // ✅ email 추가
        normalUser.setPassword(passwordEncoder.encode("password")); // 비밀번호 암호화
        normalUser.setRole(Role.ROLE_USER);
        userRepository.saveAndFlush(normalUser); // DB에 반영
    }

    /**
     * 1️⃣ 인증 없이 보호된 API 호출 시 → 403 Forbidden
     */
    @Test
    void givenNoAuthentication_whenAccessingProtectedApi_thenForbidden() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }

    /**
     * 2️⃣ 일반 사용자 (`ROLE_USER`)가 사용자 API 호출 시 → 200 OK
     */
    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void givenUserRole_whenAccessingUserApi_thenOk() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk());
    }

    /**
     * 3️⃣ 일반 사용자 (`ROLE_USER`)가 관리자 API 호출 시 → 403 Forbidden
     */
    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void givenUserRole_whenAccessingAdminApi_thenForbidden() throws Exception {
        mockMvc.perform(get("/admin/policy"))
                .andExpect(status().isForbidden());
    }

    /**
     * 5️⃣ JWT 기반 로그인 후 유효한 토큰 반환
     */
    @Test
    void givenValidCredentials_whenLoggingIn_thenReturnsJwtToken() throws Exception {
        String requestBody = """
                {
                    "username": "user1",
                    "password": "password"
                }
                """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(response).contains("token");
    }
}
