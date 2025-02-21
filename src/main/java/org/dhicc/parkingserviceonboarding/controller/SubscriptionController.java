package org.dhicc.parkingserviceonboarding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.SubscriptionDTO;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
@Tag(name = "정기권 API", description = "정기권 등록 및 관리 API")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Operation(summary = "내 정기권 조회", description = "로그인한 사용자의 정기권 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "정기권 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<SubscriptionDTO> getMySubscription(@AuthenticationPrincipal UserDetails userDetails) {
        SubscriptionDTO subscription = subscriptionService.getSubscriptionByUser(userDetails.getUsername());
        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subscription);
    }


    @Operation(summary = "정기권 등록", description = "차량 번호와 기간을 입력해 정기권을 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정기권 등록 성공"),
            @ApiResponse(responseCode = "409", description = "이미 등록된 차량 또는 사용자가 이미 정기권 보유")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerSubscription(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SubscriptionDTO subscriptionDTO) {
        try {
            Subscription subscription = subscriptionService.registerSubscription(
                    userDetails.getUsername(),
                    subscriptionDTO.getVehicleNumber(),
                    subscriptionDTO.getStartDate(),
                    subscriptionDTO.getEndDate()
            );
            SubscriptionDTO responseDTO = new SubscriptionDTO(
                    subscription.getVehicleNumber(),
                    subscription.getStartDate(),
                    subscription.getEndDate()
            );
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @Operation(summary = "정기권 취소 (관리자만 가능)", description = "차량 번호를 이용해 정기권을 취소 (관리자 권한 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정기권 취소 성공"),
            @ApiResponse(responseCode = "400", description = "정기권이 존재하지 않음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{vehicleNumber}")
    public ResponseEntity<?> cancelSubscription(@PathVariable String vehicleNumber) {
        try {
            subscriptionService.cancelSubscription(vehicleNumber);
            return ResponseEntity.ok("정기권이 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(409).body(Collections.singletonMap("error", ex.getMessage()));
    }
}
