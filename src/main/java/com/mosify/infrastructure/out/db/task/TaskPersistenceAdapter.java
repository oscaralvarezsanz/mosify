package com.mosify.infrastructure.out.db.task;

import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.domain.model.Task;
import com.mosify.infrastructure.out.db.task.mapper.TaskEntityConverter;
import com.mosify.infrastructure.out.db.model.TaskEntity;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskPersistenceAdapter implements TaskRepository {

    private final TaskJpaRepository jpaRepository;
    private final TaskEntityConverter entityConverter;

    public TaskPersistenceAdapter(TaskJpaRepository jpaRepository, TaskEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = entityConverter.toEntity(task);
        TaskEntity saved = jpaRepository.save(entity);
        return entityConverter.toDomain(saved);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaRepository.findById(id).map(entityConverter::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByCategoryId(UUID categoryId) {
        jpaRepository.deleteAllByCategoryId(categoryId);
    }

    @Override
    public void deleteAllByCategoryIdIn(List<UUID> categoryIds) {
        jpaRepository.deleteAllByCategoryIdIn(categoryIds);
    }
}
