package org.dhicc.parkingserviceonboarding.service;

import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subscriptionService = new SubscriptionService(subscriptionRepository, userRepository); // 💡 수정된 부분
    }

    @Test
    void testRegisterSubscription_Success() {
        // given
        String username = "testUser";
        String vehicleNumber = "XYZ123";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(subscriptionRepository.existsByVehicleNumber(vehicleNumber)).thenReturn(false);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Subscription result = subscriptionService.registerSubscription(username, vehicleNumber, startDate, endDate);

        // then
        assertNotNull(result);
        assertEquals(vehicleNumber, result.getVehicleNumber());
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
    }
}
