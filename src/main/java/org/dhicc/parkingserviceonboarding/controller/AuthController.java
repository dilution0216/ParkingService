package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.dto.UserRequest;
import org.dhicc.parkingserviceonboarding.security.JwtUtil;
import org.dhicc.parkingserviceonboarding.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /** ✅ 1. 회원가입 API */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
        userService.registerUser(userRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }




    /** ✅ 2. 로그인 API (JWT 발급) */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {

        // ✅ 입력된 username & password 가져오기
        String username = credentials.get("username");
        String password = credentials.get("password");

        // ✅ 인증 매니저를 통해 사용자 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // ✅ 인증된 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // ✅ JWT 토큰 생성
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // ✅ 토큰 반환
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}
