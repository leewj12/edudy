package com.kosmo.backend.global.security;

import com.kosmo.backend.user.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    // 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> user.getUserRole().name()); // ROLE_USER 등
    }

    @Override
    public String getUsername() {
        return user.getUserEmail(); // 로그인에 사용할 필드
    }

    @Override
    public String getPassword() {
        return user.getUserPwd();
    }

    // 계정 상태 관련
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}
