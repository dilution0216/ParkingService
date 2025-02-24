package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // 🟢 로그 사용 가능하도록 추가
public class ParkingRecordService {
    private final ParkingRecordRepository parkingRecordRepository;
     // 7일 이상 지난 주차 정산 내역 자동 삭제
    @Transactional
    // @Scheduled(fixedRate = 30000) // 30초마다 실행 (테스트용)
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldParkingRecords() {
        LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(7); // ✅ LocalDateTime으로 변경
        List<ParkingRecord> oldRecords = parkingRecordRepository.findByExitTimeBefore(cutoffDateTime); // ✅ LocalDateTime 사용

        if (!oldRecords.isEmpty()) {
            log.info("🗑️ [정산 삭제] {}건의 7일 이상 지난 주차 정산 내역 삭제 완료", oldRecords.size());
            parkingRecordRepository.deleteAll(oldRecords);
        } else {
            log.info("✅ [정산 유지] 7일 이상 지난 주차 정산 내역이 없습니다.");
        }
    }
}
