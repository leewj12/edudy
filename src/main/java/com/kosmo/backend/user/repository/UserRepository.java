package com.kosmo.backend.user.repository;

import com.kosmo.backend.user.entity.Role;
import com.kosmo.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByUserEmail (String email);
    Optional<UserEntity> findByRefreshToken(String refreshToken);
    Optional<UserEntity> findByUserPhone(String userPhone);
    Optional<UserEntity> findByUserPhoneAndUserIdNot(String userPhone, Long userId);
    Optional<UserEntity> findByUserEmailAndUserRoleIn(String email, Iterable<Role> roles); // ← adminLogin 용
    List<UserEntity> findByUserRole(Role role);
}
