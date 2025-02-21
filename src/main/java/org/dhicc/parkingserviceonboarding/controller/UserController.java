package org.dhicc.parkingserviceonboarding.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.UserResponse;
import org.dhicc.parkingserviceonboarding.dto.UserUpdateRequest;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.dhicc.parkingserviceonboarding.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users") // ✅ `/auth` → `/users` 변경
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /** ✅ 1. 로그인한 사용자 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse userResponse = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    /** ✅ 2. 특정 ID 사용자 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(new UserResponse(user));
    }


    /** ✅ 3. 로그인한 사용자 정보 수정 */
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest updateRequest  // ✅ @Valid 추가
    ) {
        UserResponse updatedUser = userService.updateCurrentUser(userDetails.getUsername(), updateRequest);
        return ResponseEntity.ok(updatedUser);
    }


    /** ✅ 4. 사용자 삭제 (관리자만 가능) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {  // 🔥 @PathVariable 추가
        User requestingUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!requestingUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("Only admin can delete users");
        }

        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();  // ✅ 204 No Content 반환
    }





}
