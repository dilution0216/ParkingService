package org.dhicc.parkingserviceonboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailRequest {
    private String vehicleNumber;
    private int amount;
    private String timestamp;
}
