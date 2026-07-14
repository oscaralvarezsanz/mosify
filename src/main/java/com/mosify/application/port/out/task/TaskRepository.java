package com.mosify.application.port.out.task;

import com.mosify.domain.model.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findAll();
    void deleteById(UUID id);
    void deleteAllByCategoryId(UUID categoryId);
    void deleteAllByCategoryIdIn(List<UUID> categoryIds);
    List<Task> findAllByCategoryIdIn(List<UUID> categoryIds);
}
