package com.mosify.application.port.out.category;

import com.mosify.domain.model.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    List<Category> findAll();
    void deleteById(UUID id);
    void deleteAllByUserId(UUID userId);
    void deleteAllByBoardId(UUID boardId);
    List<Category> findAllByBoardId(UUID boardId);
}
