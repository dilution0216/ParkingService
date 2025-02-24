package org.dhicc.parkingserviceonboarding.reposiotry;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {
    Optional<ParkingRecord> findByVehicleNumberAndExitTimeIsNull(String vehicleNumber);
    List<ParkingRecord> findByVehicleNumber(String vehicleNumber);

    Optional<ParkingRecord> findByVehicleNumberAndExitTimeIsNotNull(String vehicleNumber);
    List<ParkingRecord> findByExitTimeBefore(LocalDateTime dateTime);
}
