package org.dhicc.parkingserviceonboarding.controller;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.UserResponse;
import org.dhicc.parkingserviceonboarding.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // ✅ `/auth` → `/users` 변경
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** ✅ 1. 로그인한 사용자 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse userResponse = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userResponse);
    }
}
