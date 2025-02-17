package org.dhicc.parkingserviceonboarding.service;

import org.dhicc.parkingserviceonboarding.dto.EmailRequest;
import org.dhicc.parkingserviceonboarding.model.PaymentCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReceiptService {

    private final RestTemplate restTemplate;

    public ReceiptService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async  // 비동기 실행
    @EventListener
    public void sendReceiptEmail(PaymentCompletedEvent event) {
        String mockApiUrl = "https://mock-email-service.com/send-receipt";

        // Mock API 호출 (비동기)
        EmailRequest emailRequest = new EmailRequest(event.getVehicleNumber(), event.getAmount(), event.getTimestamp());
        restTemplate.postForEntity(mockApiUrl, emailRequest, String.class);

        System.out.println("비동기 영수증 이메일 발송 완료 (Mock API 호출)");
    }
}
