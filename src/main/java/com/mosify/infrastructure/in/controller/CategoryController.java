package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.WebCategoryRequest;
import com.mosify.api.model.WebCategoryResponse;
import com.mosify.application.port.in.category.CategoryCreatePort;
import com.mosify.application.port.in.category.CategoryDeletePort;
import com.mosify.application.port.in.category.CategoryGetAllPort;
import com.mosify.application.port.in.category.CategoryGetByIdPort;
import com.mosify.domain.model.Category;
import com.mosify.infrastructure.in.mapper.CategoryWebConverter;
import com.mosify.infrastructure.security.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryCreatePort categoryCreatePort;
    private final CategoryGetByIdPort categoryGetByIdPort;
    private final CategoryGetAllPort categoryGetAllPort;
    private final CategoryDeletePort categoryDeletePort;
    private final CategoryWebConverter webConverter;

    public CategoryController(CategoryCreatePort categoryCreatePort,
                              CategoryGetByIdPort categoryGetByIdPort,
                              CategoryGetAllPort categoryGetAllPort,
                              CategoryDeletePort categoryDeletePort,
                              CategoryWebConverter webConverter) {
        this.categoryCreatePort = categoryCreatePort;
        this.categoryGetByIdPort = categoryGetByIdPort;
        this.categoryGetAllPort = categoryGetAllPort;
        this.categoryDeletePort = categoryDeletePort;
        this.webConverter = webConverter;
    }

    @PostMapping
    public ResponseEntity<WebCategoryResponse> createCategory(
            @RequestBody WebCategoryRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        Category category = webConverter.toDomain(request);
        Category created = categoryCreatePort.createCategory(category, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(webConverter.toWebResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebCategoryResponse> getCategoryById(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        Category category = categoryGetByIdPort.getCategoryById(id, userId);
        return ResponseEntity.ok(webConverter.toWebResponse(category));
    }

    @GetMapping
    public ResponseEntity<List<WebCategoryResponse>> getAllCategories(
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        List<WebCategoryResponse> responses = categoryGetAllPort.getAllCategories(userId).stream()
                .map(webConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        categoryDeletePort.deleteCategory(id, userId);
        return ResponseEntity.noContent().build();
    }
}
