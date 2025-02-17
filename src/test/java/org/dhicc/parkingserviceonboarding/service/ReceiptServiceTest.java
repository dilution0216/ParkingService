package org.dhicc.parkingserviceonboarding.service;

import org.dhicc.parkingserviceonboarding.model.PaymentCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReceiptServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendReceiptEmail_CallsMockApi() {
        // Given
        PaymentCompletedEvent event = new PaymentCompletedEvent("TEST123", 4500, "2025-02-14T12:05:00");

        // When
        receiptService.sendReceiptEmail(event);

        // Then
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }
}
