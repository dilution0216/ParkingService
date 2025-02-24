package org.dhicc.parkingserviceonboarding.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionDTO {
    private String vehicleNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long userId; // 💡 사용자 ID 추가
}
