package org.dhicc.parkingserviceonboarding;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.model.PaymentCompletedEvent;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.dhicc.parkingserviceonboarding.service.ParkingService;
import org.dhicc.parkingserviceonboarding.service.PaymentService;
import org.dhicc.parkingserviceonboarding.service.ReceiptService;
import org.dhicc.parkingserviceonboarding.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EndToEndTest {

    @Autowired
    private ParkingService parkingService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ParkingRecordRepository parkingRecordRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    void testFullParkingProcess() {
        String vehicleNumber = "TEST1234";

        // 🚗 차량 입차
        ParkingRecord entryRecord = parkingService.registerEntry(vehicleNumber);
        assertNotNull(entryRecord);
        assertEquals(vehicleNumber, entryRecord.getVehicleNumber());

        // 🚙 차량 출차
        ParkingRecord exitRecord = parkingService.registerExit(vehicleNumber);
        assertNotNull(exitRecord.getExitTime());
        assertTrue(exitRecord.getFee() > 0);

        // 💳 결제 진행
        Payment payment = paymentService.processPayment(vehicleNumber, Optional.empty());
        assertNotNull(payment);
        assertEquals(vehicleNumber, payment.getVehicleNumber());

        // 📜 영수증 발송 (Mock API 활용)
        PaymentCompletedEvent paymentEvent = new PaymentCompletedEvent(
                payment.getVehicleNumber(),
                payment.getAmount(),
                payment.getTimestamp().toString()  // LocalDateTime → String 변환
        );
        assertDoesNotThrow(() -> receiptService.sendReceiptEmail(paymentEvent));

        // 🏷 정기권 등록 (LocalDateTime → LocalDate 변환)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);
        Subscription subscription = subscriptionService.registerSubscription(vehicleNumber, startDate, endDate);
        assertNotNull(subscription);
        assertEquals(vehicleNumber, subscription.getVehicleNumber());

        // ✅ 모든 과정이 정상적으로 실행되었는지 검증
        assertTrue(parkingRecordRepository.findByVehicleNumber(vehicleNumber).size() > 0);
        assertTrue(paymentRepository.findByVehicleNumber(vehicleNumber).size() > 0);
        assertTrue(subscriptionRepository.findByVehicleNumber(vehicleNumber).isPresent());
    }
}
