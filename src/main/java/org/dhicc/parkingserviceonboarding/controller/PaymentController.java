package org.dhicc.parkingserviceonboarding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(name = "결제 API", description = "주차 요금 결제 및 결제 내역 조회")
public class PaymentController {
    private final PaymentService paymentService;


    @Operation(summary = "결제 처리", description = "차량 번호를 이용해 주차 요금을 결제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 완료"),
            @ApiResponse(responseCode = "400", description = "출차 기록 없음")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/process/{vehicleNumber}")
    public ResponseEntity<Payment> processPayment(@PathVariable String vehicleNumber, @RequestParam Optional<String> couponCode) {
        return ResponseEntity.ok(paymentService.processPayment(vehicleNumber, couponCode));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "결제 내역 조회", description = "결제 ID를 이용하여 결제 내역 조회")
    @ApiResponse(responseCode = "200", description = "결제 내역 조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "모든 결제 내역 조회", description = "전체 결제 내역을 조회")
    @ApiResponse(responseCode = "200", description = "결제 내역 조회 성공")
    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", ex.getMessage()));
    }
}



