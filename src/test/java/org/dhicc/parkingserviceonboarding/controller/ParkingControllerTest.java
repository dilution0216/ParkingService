package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.dto.ParkingRecordDTO;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.dhicc.parkingserviceonboarding.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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
public class ParkingControllerTest {

    @org.springframework.beans.factory.annotation.Autowired
    private TestRestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private SubscriptionRepository subscriptionRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Autowired
    private JwtProvider jwtProvider;

    private String jwtToken;

    @BeforeEach
    @Commit // ✅ 테스트 데이터 유지
    void setUp() {
        parkingRecordRepository.deleteAll();
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();

        // ✅ 테스트용 사용자 추가
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ROLE_USER);
        userRepository.save(testUser);

        // ✅ JWT 토큰 생성
        jwtToken = jwtProvider.generateToken("testUser");
        System.out.println("✅ JWT 토큰 생성 완료: " + jwtToken);
    }

    /** ✅ 1. 차량 입차 테스트 */
    @Test
    public void testRegisterEntry_Success() {
        // Given
        String vehicleNumber = "TEST123";

        // ✅ JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<ParkingRecord> response = restTemplate.postForEntity(
                "/parking/entry/{vehicleNumber}",
                requestEntity,
                ParkingRecord.class,
                vehicleNumber
        );

        // ✅ 응답 상태 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // ✅ 입차 기록이 실제로 DB에 저장되었는지 확인
        ParkingRecord record = parkingRecordRepository
                .findByVehicleNumberAndExitTimeIsNull(vehicleNumber)
                .orElse(null);
        assertNotNull(record);
        assertEquals(vehicleNumber, record.getVehicleNumber());
        assertNotNull(record.getEntryTime()); // ✅ 입차 시간이 기록되어야 함
    }

    /** ✅ 2. 차량 출차 테스트 */
    @Test
    public void testRegisterExit_Success() {
        // Given
        String vehicleNumber = "TEST456";
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber(vehicleNumber);
        record.setEntryTime(LocalDateTime.now().minusHours(3));
        parkingRecordRepository.save(record);

        // ✅ JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<ParkingRecord> response = restTemplate.postForEntity(
                "/parking/exit/{vehicleNumber}",
                requestEntity,
                ParkingRecord.class,
                vehicleNumber
        );

        // ✅ 응답 상태 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // ✅ 출차 기록 검증
        ParkingRecord updatedRecord = parkingRecordRepository
                .findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber)
                .orElse(null);

        assertNotNull(updatedRecord);
        assertNotNull(updatedRecord.getExitTime()); // ✅ 출차 시간이 저장되었는지 확인
        assertTrue(updatedRecord.getFee() > 0); // ✅ 요금이 발생했는지 확인
    }

    /** ✅ 3. 주차 기록 조회 테스트 */
    @Test
    public void testGetParkingRecords_Success() {
        // Given
        String vehicleNumber = "TEST789";
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber(vehicleNumber);
        record.setEntryTime(LocalDateTime.now().minusHours(2));
        record.setExitTime(LocalDateTime.now());
        record.setFee(5000);
        parkingRecordRepository.save(record);

        // ✅ JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<List<ParkingRecordDTO>> response = restTemplate.exchange(
                "/parking/{vehicleNumber}",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ParkingRecordDTO>>() {},
                vehicleNumber
        );

        // ✅ 응답 상태 및 내용 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        // ✅ 응답 데이터 검증
        ParkingRecordDTO result = response.getBody().get(0);
        assertEquals(vehicleNumber, result.getVehicleNumber());
        assertEquals(5000, result.getFee());
    }
}
