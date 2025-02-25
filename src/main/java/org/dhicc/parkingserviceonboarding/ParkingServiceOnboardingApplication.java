package org.dhicc.parkingserviceonboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class ParkingServiceOnboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingServiceOnboardingApplication.class, args);
    }

}
