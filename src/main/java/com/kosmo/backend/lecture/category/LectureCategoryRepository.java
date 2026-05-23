package com.kosmo.backend.lecture.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureCategoryRepository extends JpaRepository<LectureCategoryEntity, Long> {
    Optional<LectureCategoryEntity> findByLectureCategoryName(String name);
}