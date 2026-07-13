package com.mosify.infrastructure.out.db.task;

import com.mosify.infrastructure.out.db.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {
    void deleteAllByCategoryId(UUID categoryId);
    void deleteAllByCategoryIdIn(List<UUID> categoryIds);
}
