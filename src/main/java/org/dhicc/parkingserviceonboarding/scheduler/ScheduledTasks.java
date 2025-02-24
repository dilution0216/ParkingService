package org.dhicc.parkingserviceonboarding.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dhicc.parkingserviceonboarding.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final SubscriptionService subscriptionService;

    /**
     * 매일 자정(00:00)에 실행되어 만료 예정 정기권 사용자에게 알림을 보냄.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void notifyExpiringSubscriptions() {
        log.info("🔔 정기권 만료 예정 사용자에게 알림을 보냅니다.");
        subscriptionService.notifyExpiringSubscriptions();
    }

    /**
     * 매일 자정(00:00)에 실행되어 만료된 정기권을 처리함.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void processExpiredSubscriptions() {
        log.info("🛠 만료된 정기권을 처리합니다.");
        subscriptionService.expireSubscriptions();
    }
}
