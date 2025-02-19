package org.dhicc.parkingserviceonboarding.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,  // 일반 사용자
    ROLE_ADMIN; // 관리자

    @Override
    public String getAuthority() {
        return name();
    }
}
