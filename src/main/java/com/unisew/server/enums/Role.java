package com.unisew.server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("admin"),
    DESIGNER("designer"),
    SCHOOL("school"),
    GARMENT("garment");

    private final String value;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(
                "ROLE_" + this.name().toLowerCase()));
    }
}
