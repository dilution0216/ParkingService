package org.dhicc.parkingserviceonboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dhicc.parkingserviceonboarding.model.Role;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private Role role;
}
