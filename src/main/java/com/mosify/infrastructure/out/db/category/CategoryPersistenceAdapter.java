package com.mosify.infrastructure.out.db.category;

import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.domain.model.Category;
import com.mosify.infrastructure.out.db.category.mapper.CategoryEntityConverter;
import com.mosify.infrastructure.out.db.model.CategoryEntity;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryEntityConverter entityConverter;

    public CategoryPersistenceAdapter(CategoryJpaRepository jpaRepository, CategoryEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = entityConverter.toEntity(category);
        CategoryEntity saved = jpaRepository.save(entity);
        return entityConverter.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(entityConverter::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        jpaRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteAllByBoardId(UUID boardId) {
        jpaRepository.deleteAllByBoardId(boardId);
    }

    @Override
    public List<Category> findAllByBoardId(UUID boardId) {
        return jpaRepository.findAllByBoardId(boardId).stream()
                .map(entityConverter::toDomain)
                .toList();
    }
}
