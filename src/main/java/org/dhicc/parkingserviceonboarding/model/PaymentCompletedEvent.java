package org.dhicc.parkingserviceonboarding.model;

import lombok.Getter;

@Getter
public class PaymentCompletedEvent {
    private final String vehicleNumber;
    private final int amount;
    private final String timestamp;

    public PaymentCompletedEvent(String vehicleNumber, int amount, String timestamp) {
        this.vehicleNumber = vehicleNumber;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
