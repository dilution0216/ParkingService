package org.dhicc.parkingserviceonboarding.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name(); // ✅ Enum 이름(ROLE_USER, ROLE_ADMIN)을 그대로 반환
    }
}
