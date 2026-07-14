package com.mosify.infrastructure.out.db.category;

import com.mosify.infrastructure.out.db.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    void deleteAllByUserId(UUID userId);
    void deleteAllByBoardId(UUID boardId);
    List<CategoryEntity> findAllByBoardId(UUID boardId);
}
