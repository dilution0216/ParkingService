package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.dhicc.parkingserviceonboarding.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class PaymentControllerTest {

    @org.springframework.beans.factory.annotation.Autowired
    private TestRestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private PaymentRepository paymentRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private SubscriptionRepository subscriptionRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Autowired
    private JwtProvider jwtProvider;

    private String jwtToken;
    private String adminToken;
    private Long paymentId;

    @BeforeEach
    @Commit
    void setUp() {
        paymentRepository.deleteAll();
        parkingRecordRepository.deleteAll();
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();

        // ✅ 테스트 사용자 생성 및 저장
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ROLE_USER); // ✅ Role Enum 값 직접 설정
        userRepository.save(testUser);

        User adminUser = new User();
        adminUser.setUsername("adminUser");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ROLE_ADMIN); // ✅ Role Enum 값 직접 설정
        userRepository.save(adminUser);

        // ✅ JWT 토큰 생성
        jwtToken = jwtProvider.generateToken("testUser");
        adminToken = jwtProvider.generateToken("adminUser");

        // ✅ 차량 출차 기록 추가 (결제 가능하도록 설정)
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber("123ABC");
        record.setEntryTime(LocalDateTime.now().minusHours(2)); // 2시간 전 입차
        record.setExitTime(LocalDateTime.now()); // 현재 출차
        record.setFee(5000); // 예상 요금 5000원
        parkingRecordRepository.save(record);
    }


    /** ✅ 1. 결제 처리 테스트 */
    @Test
    public void testProcessPayment_Success() {
        // Given
        String vehicleNumber = "123ABC";

        // ✅ JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Payment> response = restTemplate.postForEntity(
                "/payment/process/{vehicleNumber}",
                requestEntity,
                Payment.class,
                vehicleNumber
        );

        // ✅ 응답 상태 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // ✅ 결제 금액 검증
        assertEquals(5000, response.getBody().getAmount());

        // ✅ 저장된 결제 내역 확인
        paymentId = response.getBody().getId();
        Payment savedPayment = paymentRepository.findById(paymentId).orElse(null);
        assertNotNull(savedPayment);
        assertEquals(5000, savedPayment.getAmount());
    }

    /** ✅ 2. 결제 내역 조회 테스트 (USER 권한) */
    @Test
    public void testGetPaymentById_Success_AsUser() {
        // ✅ 결제 처리 먼저 수행
        testProcessPayment_Success();

        // ✅ JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Payment> response = restTemplate.exchange(
                "/payment/{id}",
                HttpMethod.GET,
                requestEntity,
                Payment.class,
                paymentId
        );

        // ✅ 응답 상태 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // ✅ 결제 내역 검증
        assertEquals(5000, response.getBody().getAmount());
    }

    /** ✅ 3. 전체 결제 내역 조회 테스트 (ADMIN 권한) */
    @Test
    public void testGetAllPayments_AsAdmin() {
        // ✅ 결제 처리 먼저 수행
        testProcessPayment_Success();

        // ✅ JWT 추가 (Admin 권한)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<List<Payment>> response = restTemplate.exchange(
                "/payment/all",
                HttpMethod.GET,
                requestEntity,
                new org.springframework.core.ParameterizedTypeReference<List<Payment>>() {}
        );

        // ✅ 응답 상태 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        // ✅ 결제 내역 검증
        Payment savedPayment = response.getBody().get(0);
        assertEquals(5000, savedPayment.getAmount());
    }

    /** ✅ 4. 권한 없는 사용자 요청 시 실패 */
    @Test
    public void testGetAllPayments_AsUser_Fail() {
        // ✅ JWT 추가 (USER 권한)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<List<Payment>> response = restTemplate.exchange(
                "/payment/all",
                HttpMethod.GET,
                requestEntity,
                new org.springframework.core.ParameterizedTypeReference<List<Payment>>() {}
        );

        // ✅ 응답 상태 검증 (403 FORBIDDEN)
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
