package org.dhicc.parkingserviceonboarding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;


@Entity
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 정기권 차량 번호
    @Column(nullable = false, unique = true)
    private String vehicleNumber;
    // 정기권 시작과 종료 날짜
    private LocalDate startDate;
    private LocalDate endDate;


    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParkingRecord> parkingRecords;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true) // 한 명의 User당 하나의 Subscription만 가능
    private User user;
}