package org.dhicc.parkingserviceonboarding.dto;

import lombok.Getter;
import lombok.Setter;
import org.dhicc.parkingserviceonboarding.model.Role;

@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
}