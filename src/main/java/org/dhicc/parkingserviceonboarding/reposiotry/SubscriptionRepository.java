package org.dhicc.parkingserviceonboarding.reposiotry;

import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByVehicleNumber(String vehicleNumber);
    boolean existsByVehicleNumber(String vehicleNumber);
    Optional<Subscription> findByUser(User user);
    List<Subscription> findByEndDate(LocalDate endDate);
    List<Subscription> findByEndDateBefore(LocalDate date);
}
