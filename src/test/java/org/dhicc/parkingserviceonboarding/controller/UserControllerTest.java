package org.dhicc.parkingserviceonboarding.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private String jwtToken;

    @BeforeEach
    void setUp() {
        System.out.println("BeforeEach 실행: testUser를 DB에 삽입합니다.");

        userRepository.deleteAll();  // ✅ 기존 데이터 삭제 후 초기화

        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("testPassword"));  // ✅ 암호화된 비밀번호 저장
        testUser.setRole(Role.ROLE_USER);  // ✅ Role 값 확인

        userRepository.saveAndFlush(testUser);  // ✅ DB에 강제 반영
        entityManager.clear();  // ✅ 영속성 컨텍스트 초기화 (쿼리 실행 시 DB에서 직접 가져오도록)

        // 🔥 `generateToken(String username)` 호출하여 JWT 생성
        jwtToken = "Bearer " + jwtProvider.generateToken(testUser.getUsername());
    }


    @Test
    void testGetCurrentUser() throws Exception {
        System.out.println("테스트 실행: testUser가 정상적으로 조회되는지 확인합니다.");

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)  // ✅ JWT 토큰 포함
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // ✅ 403 문제 해결 기대
                .andExpect(jsonPath("$.username").value("testUser"));
    }
}
