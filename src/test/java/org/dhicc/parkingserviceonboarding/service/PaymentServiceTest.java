package org.dhicc.parkingserviceonboarding.service;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.PaymentCompletedEvent;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ParkingRecordRepository parkingRecordRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private DiscountCouponRepository discountCouponRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPayment_PublishesEvent() {
        // Given
        String vehicleNumber = "TEST123";
        int fee = 5000;
        LocalDateTime now = LocalDateTime.now();

        // 🚗 출차 기록이 존재하도록 Mock 설정
        ParkingRecord mockRecord = new ParkingRecord();
        mockRecord.setVehicleNumber(vehicleNumber);
        mockRecord.setFee(fee);
        mockRecord.setExitTime(now);

        Payment payment = new Payment();
        payment.setVehicleNumber(vehicleNumber);
        payment.setAmount(fee);
        payment.setTimestamp(now);

        when(parkingRecordRepository.findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber))
                .thenReturn(Optional.of(mockRecord)); // ✅ 출차 기록 반환

        when(subscriptionRepository.findByVehicleNumber(vehicleNumber)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        paymentService.processPayment(vehicleNumber, Optional.empty());

        // Then
        ArgumentCaptor<PaymentCompletedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        PaymentCompletedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(vehicleNumber, publishedEvent.getVehicleNumber());
        assertEquals(fee, publishedEvent.getAmount());
        assertNotNull(publishedEvent.getTimestamp());
    }

}





















//package org.dhicc.parkingserviceonboarding.service;
//
//
//import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
//import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
//import org.dhicc.parkingserviceonboarding.model.Payment;
//import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
//import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
//import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
//import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceTest {
//
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @Mock
//    private ParkingRecordRepository parkingRecordRepository;
//
//    @Mock
//    private SubscriptionRepository subscriptionRepository;
//
//    @Mock
//    private DiscountCouponRepository discountCouponRepository;
//
//    @InjectMocks
//    private PaymentService paymentService;
//
//    @Test
//    void testProcessPaymentWithDiscount() {
//        String vehicleNumber = "5678CD";
//        String couponCode = "DISCOUNT20";
//
//        ParkingRecord record = new ParkingRecord();
//        record.setVehicleNumber(vehicleNumber);
//        record.setExitTime(LocalDateTime.now());
//        record.setFee(10000);
//
//        DiscountCoupon mockCoupon = new DiscountCoupon();
//        mockCoupon.setCouponCode(couponCode);
//        mockCoupon.setDiscountRate(20); // 20% 할인 적용
//
//        when(parkingRecordRepository.findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber)).thenReturn(record);
//        when(subscriptionRepository.findByVehicleNumber(vehicleNumber)).thenReturn(null);
//        when(discountCouponRepository.findByCouponCode(couponCode)).thenReturn(mockCoupon);
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0)); // 정상적인 위치에 배치
//
//        // processPayment 호출
//        Payment payment = paymentService.processPayment(vehicleNumber, Optional.of(couponCode));
//
//        // 결과 검증
//        assertNotNull(payment);
//        assertTrue(payment.getAmount() < 10000); // 쿠폰 적용 후 금액이 10000원 미만인지 확인
//        assertTrue(payment.getDiscountDetails().contains("쿠폰 할인")); // 쿠폰 적용 여부 확인
//    }
//}