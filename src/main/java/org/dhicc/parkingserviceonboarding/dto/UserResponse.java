package org.dhicc.parkingserviceonboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dhicc.parkingserviceonboarding.model.Role;
import org.dhicc.parkingserviceonboarding.model.User; // User import 추가

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private Role role;

    // 추가: User 객체를 받는 생성자
    public UserResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
