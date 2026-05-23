package com.kosmo.backend.user.dto;

import com.kosmo.backend.user.entity.Role;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponse {
    private Long userId;
    private String usersName;
    private String userEmail;
    private String userPhone;
    private String userGender;
    private LocalDate userBirth;
    private Role userRole;
}
