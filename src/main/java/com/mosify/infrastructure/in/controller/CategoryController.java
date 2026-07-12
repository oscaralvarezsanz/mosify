package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.WebCategoryRequest;
import com.mosify.api.model.WebCategoryResponse;
import com.mosify.application.port.in.category.CategoryCreatePort;
import com.mosify.application.port.in.category.CategoryGetAllPort;
import com.mosify.application.port.in.category.CategoryGetByIdPort;
import com.mosify.domain.model.Category;
import com.mosify.infrastructure.in.mapper.CategoryWebConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryCreatePort categoryCreatePort;
    private final CategoryGetByIdPort categoryGetByIdPort;
    private final CategoryGetAllPort categoryGetAllPort;
    private final CategoryWebConverter webConverter;

    public CategoryController(CategoryCreatePort categoryCreatePort,
                              CategoryGetByIdPort categoryGetByIdPort,
                              CategoryGetAllPort categoryGetAllPort,
                              CategoryWebConverter webConverter) {
        this.categoryCreatePort = categoryCreatePort;
        this.categoryGetByIdPort = categoryGetByIdPort;
        this.categoryGetAllPort = categoryGetAllPort;
        this.webConverter = webConverter;
    }

    @PostMapping
    public ResponseEntity<WebCategoryResponse> createCategory(@RequestBody WebCategoryRequest request) {
        Category category = webConverter.toDomain(request);
        Category created = categoryCreatePort.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(webConverter.toWebResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebCategoryResponse> getCategoryById(@PathVariable UUID id) {
        Category category = categoryGetByIdPort.getCategoryById(id);
        return ResponseEntity.ok(webConverter.toWebResponse(category));
    }

    @GetMapping
    public ResponseEntity<List<WebCategoryResponse>> getAllCategories() {
        List<WebCategoryResponse> responses = categoryGetAllPort.getAllCategories().stream()
                .map(webConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
