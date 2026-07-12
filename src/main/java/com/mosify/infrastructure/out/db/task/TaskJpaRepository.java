package com.mosify.infrastructure.out.db.task;

import com.mosify.infrastructure.out.db.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {
}
