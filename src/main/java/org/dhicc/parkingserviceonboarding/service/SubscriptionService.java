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

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
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
                        subscription.getEndDate()
                ))
                .orElse(null);  // 정기권이 없으면 null 반환
    }
}
