package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.UserRequest;
import org.dhicc.parkingserviceonboarding.dto.UserResponse;
import org.dhicc.parkingserviceonboarding.dto.UserUpdateRequest;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("userService")  // ✅ SecurityContext에서 찾을 수 있도록 명시적으로 네이밍 유지
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    /** ✅ 1. 로그인한 사용자 정보 조회 */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        return new UserResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    /** ✅ 2. 사용자 등록 */
    public UserResponse registerUser(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // ✅ 비밀번호 암호화 적용
        user.setEmail(userRequest.getEmail());
        user.setRole(userRequest.getRole());

        userRepository.save(user);
        return new UserResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    /** ✅ 3. 로그인한 사용자 정보 수정 */
    @Transactional
    public UserResponse updateCurrentUser(String username, UserUpdateRequest updateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        if (updateRequest.getUsername() != null && !updateRequest.getUsername().isBlank()) {
            user.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isBlank()) {
            user.setEmail(updateRequest.getEmail());
        }

        return new UserResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    /** ✅ 4. 유저 삭제 (관리자 권한 필요) */
    @Transactional
    public void deleteUserById(String adminUsername, Long userId) {
        User adminUser = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new UsernameNotFoundException("관리자를 찾을 수 없습니다: " + adminUsername));

        // ✅ 관리자 권한이 없으면 예외 발생
        if (adminUser.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("관리자만 사용자 삭제가 가능합니다.");
        }

        // ✅ 삭제할 유저가 존재하는지 확인
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + userId);
        }

        userRepository.deleteById(userId);
    }


    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + id));
        return new UserResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }



}
