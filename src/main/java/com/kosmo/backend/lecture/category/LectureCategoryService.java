package com.kosmo.backend.lecture.category;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.category.dto.LectureCategoryCreateRequest;
import com.kosmo.backend.lecture.category.dto.LectureCategoryResponse;
import com.kosmo.backend.lecture.category.dto.LectureCategoryUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureCategoryService {

    private final LectureCategoryRepository categoryRepository;

    @Transactional
    public void createCategory(LectureCategoryCreateRequest request) {
        if (categoryRepository.findByLectureCategoryName(request.getLectureCategoryName()).isPresent()) {
            throw new CustomAuthException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        LectureCategoryEntity category = LectureCategoryEntity.builder()
                .lectureCategoryName(request.getLectureCategoryName())
                .build();

        categoryRepository.save(category);
    }

    public List<LectureCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new LectureCategoryResponse(c.getLectureCategoryId(), c.getLectureCategoryName()))
                .toList();
    }

    @Transactional
    public void updateCategory(Long id, LectureCategoryUpdateRequest request) {
        LectureCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.CATEGORY_NOT_FOUND));

        // 중복 이름 체크 (자기 자신 제외)
        if (categoryRepository.findByLectureCategoryName(request.getLectureCategoryName())
                .filter(c -> !c.getLectureCategoryId().equals(id)).isPresent()) {
            throw new CustomAuthException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        category.updateName(request.getLectureCategoryName());
    }

    @Transactional
    public void deleteCategory(Long id) {
        LectureCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category); // preRemove가 여기서 작동함
    }
}