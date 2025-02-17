package org.dhicc.parkingserviceonboarding;
import org.dhicc.parkingserviceonboarding.model.*;
import org.dhicc.parkingserviceonboarding.reposiotry.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParkingIntegrationTest {

    @Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @Test
    void testParkingFlow() {
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber("TEST1234");
        record.setEntryTime(LocalDateTime.now().minusHours(2));
        record.setExitTime(LocalDateTime.now());
        record.setFee(4000);

        parkingRecordRepository.save(record);

        Optional<ParkingRecord> savedRecord = parkingRecordRepository.findById(record.getId());

        assertTrue(savedRecord.isPresent());
        assertEquals(4000, savedRecord.get().getFee());
    }
}

@SpringBootTest
class SubscriptionIntegrationTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    void testSubscriptionFlow() {
        Subscription subscription = new Subscription();
        subscription.setVehicleNumber("TEST5678");
        subscription.setStartDate(LocalDateTime.now().toLocalDate());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1).toLocalDate());

        subscriptionRepository.save(subscription);

        Optional<Subscription> savedSubscription = subscriptionRepository.findByVehicleNumber("TEST5678");

        assertTrue(savedSubscription.isPresent());
        assertEquals("TEST5678", savedSubscription.get().getVehicleNumber());
    }
}

@SpringBootTest
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void testPaymentFlow() {
        Payment payment = new Payment();
        payment.setVehicleNumber("TEST9876");
        payment.setAmount(5000);
        payment.setTimestamp(LocalDateTime.now());

        paymentRepository.save(payment);

        Optional<Payment> savedPayment = paymentRepository.findById(payment.getId());

        assertTrue(savedPayment.isPresent());
        assertEquals(5000, savedPayment.get().getAmount());
    }
}

@SpringBootTest
class DiscountIntegrationTest {

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    @Test
    void testDiscountFlow() {
        DiscountCoupon coupon = new DiscountCoupon();
        coupon.setCouponCode("WELCOME10");
        coupon.setDiscountRate(10);

        discountCouponRepository.save(coupon);

        Optional<DiscountCoupon> savedCoupon = discountCouponRepository.findByCouponCode("WELCOME10");

        assertTrue(savedCoupon.isPresent());
        assertEquals(10, savedCoupon.get().getDiscountRate());
    }
}
