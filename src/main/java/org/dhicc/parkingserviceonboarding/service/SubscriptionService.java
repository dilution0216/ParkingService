package org.dhicc.parkingserviceonboarding.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.SubscriptionDTO;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Subscription registerSubscription(String username, String vehicleNumber, LocalDate startDate, LocalDate endDate) {
        if (subscriptionRepository.existsByVehicleNumber(vehicleNumber)) {
            throw new IllegalStateException("이미 등록된 차량 번호입니다: " + vehicleNumber);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        if (subscriptionRepository.findByUser(user).isPresent()) {
            throw new IllegalStateException("사용자는 하나의 정기권만 가질 수 있습니다.");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setVehicleNumber(vehicleNumber);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void cancelSubscription(String vehicleNumber) {
        Subscription subscription = subscriptionRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> new IllegalStateException("해당 차량 번호의 정기권이 존재하지 않습니다: " + vehicleNumber));

        subscriptionRepository.delete(subscription);
    }

    /**
     * 🚀 **사용자의 정기권 정보 조회 및 DTO 변환 추가**
     */
    public SubscriptionDTO getSubscriptionByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return subscriptionRepository.findByUser(user)
                .map(subscription -> new SubscriptionDTO(
                        subscription.getVehicleNumber(),
                        subscription.getStartDate(),
                        subscription.getEndDate(),
                        user.getId()  // 💡 userId 추가
                ))
                .orElse(null);
    }


    /**
     * 만료 예정 정기권 사용자에게 알림을 보냄 (로그 출력)
     */
    @Transactional
    public void notifyExpiringSubscriptions() {
        LocalDate warningDate = LocalDate.now().plusDays(3);
        List<Subscription> expiringSubscriptions = subscriptionRepository.findByEndDate(warningDate);

        for (Subscription subscription : expiringSubscriptions) {
            log.info("🚨 [알림] {} 차량의 정기권이 {}에 만료됩니다.", subscription.getVehicleNumber(), subscription.getEndDate());
            // TODO: 이메일 발송 기능 추가 가능
        }
    }

    /**
     * 만료된 정기권을 '만료됨' 상태로 변경
     */
    @Transactional
    public void expireSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscription> expiredSubscriptions = subscriptionRepository.findByEndDateBefore(today);

        for (Subscription subscription : expiredSubscriptions) {
            log.info("🔴 [만료 처리] {} 차량의 정기권이 만료되었습니다.", subscription.getVehicleNumber());
            subscriptionRepository.delete(subscription);
        }
    }
}
