package com.mosify.application.service;

import com.mosify.application.port.in.task.TaskCreatePort;
import com.mosify.application.port.in.task.TaskGetAllPort;
import com.mosify.application.port.in.task.TaskGetByIdPort;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.Task;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService implements TaskCreatePort, TaskGetByIdPort, TaskGetAllPort {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Task createTask(Task task) {
        if (isValidCategory(task))
            throw new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + task.getCategoryId());

        Boolean isActive = task.getActive() != null ? task.getActive() : true;
        Task taskToSave = task.toBuilder().active(isActive).build();
        return taskRepository.save(taskToSave);
    }

    private boolean isValidCategory(Task task) {
        return task.getCategoryId() == null || !categoryRepository.findById(task.getCategoryId()).isPresent();
    }

    @Override
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Task not found with id: " + id));
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
