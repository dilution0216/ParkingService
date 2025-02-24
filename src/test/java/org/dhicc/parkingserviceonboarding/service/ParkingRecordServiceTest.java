package org.dhicc.parkingserviceonboarding.service;


import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ParkingRecordServiceTest {

    @Autowired
    private ParkingRecordService parkingRecordService;

    @Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @BeforeEach
    void setup() {
        parkingRecordRepository.deleteAll();

        // 🚗 10일 전 출차한 기록 (삭제 대상)
        ParkingRecord oldRecord = new ParkingRecord();
        oldRecord.setVehicleNumber("123가4567");
        oldRecord.setExitTime(LocalDateTime.now().minusDays(10));
        parkingRecordRepository.saveAndFlush(oldRecord);

        // 🚗 3일 전 출차한 기록 (삭제 대상 아님)
        ParkingRecord recentRecord = new ParkingRecord();
        recentRecord.setVehicleNumber("234나5678");
        recentRecord.setExitTime(LocalDateTime.now().minusDays(3));
        parkingRecordRepository.saveAndFlush(recentRecord);

        // 🚗 출차 시간이 없는 기록 (삭제 대상 아님)
        ParkingRecord activeRecord = new ParkingRecord();
        activeRecord.setVehicleNumber("345다6789");
        activeRecord.setExitTime(null);
        parkingRecordRepository.saveAndFlush(activeRecord);
    }

    @Test
    void deleteOldParkingRecords_shouldDeleteOnlyRecordsOlderThan7Days() {
        // 🟢 Given: 초기 데이터 개수 확인
        long totalBefore = parkingRecordRepository.count();
        System.out.println("🟢 삭제 전 총 데이터 개수: " + totalBefore);
        System.out.println("🟢 삭제 전 데이터 목록: " + parkingRecordRepository.findAll());

        // 🔵 When: deleteOldParkingRecords 실행
        parkingRecordService.deleteOldParkingRecords();

        // 🔴 Then: 7일 이상 지난 기록은 삭제되어야 함
        List<ParkingRecord> remainingRecords = parkingRecordRepository.findAll();
        System.out.println("🔴 삭제 후 데이터 목록: " + remainingRecords);

        // 🟡 전체 데이터 개수 확인 (삭제 전: 3개 → 삭제 후: 2개)
        assertThat(remainingRecords).hasSize(2);

        // 🟣 삭제되지 않은 차량 번호만 존재해야 함
        assertThat(remainingRecords)
                .extracting(ParkingRecord::getVehicleNumber)
                .containsExactlyInAnyOrder("234나5678", "345다6789");

        // 🚗 10일 전 출차한 기록이 삭제되었는지 확인
        boolean isDeleted = remainingRecords.stream()
                .noneMatch(record -> record.getVehicleNumber().equals("123가4567"));
        assertThat(isDeleted).isTrue();
    }
}
