package org.dhicc.parkingserviceonboarding.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // ✅ 랜덤 포트 설정으로 Security 로딩 문제 해결
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional // ✅ 각 테스트 후 데이터 롤백
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String userJson = """
        {
            "username": "testUser",
            "password": "Test@1234",
            "email": "test@example.com",
            "role": "ROLE_USER"
        }
    """;

    @BeforeEach
    void setUp() throws Exception {
        // ✅ 사용자 등록 (회원가입 API 호출)
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
    }

    /** ✅ 1. 회원가입 테스트 */
    @Test
    void testRegisterUser() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "newUser",
                                "password": "New@1234",
                                "email": "new@example.com",
                                "role": "ROLE_USER"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입이 완료되었습니다."));
    }

    /** ✅ 2. 로그인 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"}) // ✅ Spring Security 인증 우회
    void testLogin() throws Exception {
        String loginJson = """
            {
                "username": "testUser",
                "password": "Test@1234"
            }
        """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());  // ✅ JWT 토큰이 응답에 포함되어 있는지 확인
    }
}
