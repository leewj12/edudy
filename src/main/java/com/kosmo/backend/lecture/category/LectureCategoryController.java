package com.kosmo.backend.lecture.category;


import com.kosmo.backend.lecture.category.dto.LectureCategoryCreateRequest;
import com.kosmo.backend.lecture.category.dto.LectureCategoryResponse;
import com.kosmo.backend.lecture.category.dto.LectureCategoryUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureCategoryController {

    private final LectureCategoryService categoryService;

    @PostMapping("/admin/category")
    public ResponseEntity<?> createCategory(@RequestBody LectureCategoryCreateRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.ok("카테고리 생성 완료");
    }

    @GetMapping("/admin/category/list")
    public ResponseEntity<List<LectureCategoryResponse>> getAllCategories() {
        List<LectureCategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/guest/category/list")
    public ResponseEntity<List<LectureCategoryResponse>> getAllCategoriesGuest() {
        List<LectureCategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PatchMapping("/admin/category/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId,
                                            @RequestBody LectureCategoryUpdateRequest request) {
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok("카테고리 수정 완료");
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("카테고리 삭제 완료");
    }
}