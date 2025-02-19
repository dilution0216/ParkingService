package org.dhicc.parkingserviceonboarding.dto;

import lombok.Getter;
import lombok.Setter;
import org.dhicc.parkingserviceonboarding.model.Role;

@Getter
@Setter
public class UserResponse {
    private String username;
    private String email;
    private Role role;
}
