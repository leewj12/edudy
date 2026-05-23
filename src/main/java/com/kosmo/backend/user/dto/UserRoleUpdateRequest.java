package com.kosmo.backend.user.dto;

import com.kosmo.backend.user.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleUpdateRequest {
    private Role newRole;
}