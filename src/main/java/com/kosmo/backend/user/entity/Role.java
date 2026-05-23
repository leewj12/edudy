package com.kosmo.backend.user.entity;

public enum Role {
    ADMIN, USER, INSTRUCTOR;

    public String getAuthority() {
        return "ROLE_" + this.name(); // Spring Security용
    }
}
