package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.dhicc.parkingserviceonboarding.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class DiscountControllerTest {

    @org.springframework.beans.factory.annotation.Autowired
    private TestRestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Autowired
    private DiscountCouponRepository discountCouponRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Autowired
    private JwtProvider jwtProvider;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        // ✅ 기존 데이터 삭제
        discountCouponRepository.deleteAll();
        userRepository.deleteAll();

        // ✅ 테스트용 사용자 추가
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ROLE_USER); // ✅ Enum으로 설정
        userRepository.save(testUser);

        // ✅ 쿠폰 추가
        DiscountCoupon coupon = new DiscountCoupon();
        coupon.setCouponCode("DISCOUNT10");
        coupon.setDiscountRate(10);
        discountCouponRepository.save(coupon);

        // ✅ JWT 토큰 생성
        jwtToken = jwtProvider.generateToken("testUser");
        System.out.println("✅ JWT 토큰 생성 완료: " + jwtToken);
    }

    @Test
    public void testApplyDiscount_Success() {
        // Given
        String couponCode = "DISCOUNT10";
        int fee = 10000;
        int expectedDiscountedFee = 9000;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken); // ✅ JWT 추가
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/discount/apply/{couponCode}/{fee}",
                requestEntity,
                Map.class,
                couponCode, fee
        );

        System.out.println("✅ 응답 코드: " + response.getStatusCode());
        System.out.println("✅ 응답 본문: " + response.getBody());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDiscountedFee, response.getBody().get("discountedFee"));
    }
}
