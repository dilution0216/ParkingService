package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.PaymentCompletedEvent;
import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DiscountCouponRepository discountCouponRepository;
    private final ApplicationEventPublisher eventPublisher; //  이벤트 발행기 추가

    public Payment processPayment(String vehicleNumber, Optional<String> couponCode) {
        // 출차 기록 확인
        ParkingRecord record = parkingRecordRepository.findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량의 출차 기록이 없습니다."));

        // 정기권 차량인지 확인 (정기권 차량은 결제 X)
        if (subscriptionRepository.findByVehicleNumber(vehicleNumber).isPresent()) {
            return null; // 정기권 차량이면 결제 없이 null 반환
        }

        int finalFee = record.getFee();
        String discountDetails = "기본 요금 적용";

        // 할인 쿠폰 적용
        if (couponCode.isPresent()) {
            DiscountCoupon coupon = discountCouponRepository.findByCouponCode(couponCode.get())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 쿠폰 코드입니다."));

            int discountAmount = (finalFee * coupon.getDiscountRate()) / 100;
            finalFee -= discountAmount;
            discountDetails = "쿠폰 할인 적용: " + couponCode.get() + " (" + coupon.getDiscountRate() + "%)";
        }

        // 결제 처리
        Payment payment = new Payment();
        payment.setVehicleNumber(vehicleNumber);
        payment.setAmount(finalFee);
        payment.setDiscountDetails(discountDetails);
        payment.setTimestamp(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        //  결제 완료 이벤트 발행 (비동기 영수증 발송을 위해)
        eventPublisher.publishEvent(new PaymentCompletedEvent(savedPayment.getVehicleNumber(), savedPayment.getAmount(), savedPayment.getTimestamp().toString()));

        return savedPayment;
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역을 찾을 수 없습니다."));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
