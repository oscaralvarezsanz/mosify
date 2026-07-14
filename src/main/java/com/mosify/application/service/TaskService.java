package com.mosify.application.service;

import com.mosify.application.port.in.task.TaskCreatePort;
import com.mosify.application.port.in.task.TaskDeletePort;
import com.mosify.application.port.in.task.TaskGetAllPort;
import com.mosify.application.port.in.task.TaskGetByIdPort;
import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.BoardUser;
import com.mosify.domain.model.Category;
import com.mosify.domain.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService implements TaskCreatePort, TaskGetByIdPort, TaskGetAllPort, TaskDeletePort {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BoardUserRepository boardUserRepository;

    public TaskService(TaskRepository taskRepository, 
                       CategoryRepository categoryRepository, 
                       TransactionRepository transactionRepository,
                       BoardUserRepository boardUserRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.boardUserRepository = boardUserRepository;
    }

    private void checkMembership(UUID boardId, UUID userId) {
        boolean hasMembers = !boardUserRepository.findAllByBoardId(boardId).isEmpty();
        if (!hasMembers) {
            return;
        }
        boolean isMember = boardUserRepository.findByBoardIdAndUserId(boardId, userId).isPresent();
        if (!isMember) {
            throw new MosifyException(ErrorCode.FORBIDDEN, "Access denied. User is not a member of this board.");
        }
    }

    @Override
    public Task createTask(Task task, UUID userId) {
        Category category = categoryRepository.findById(task.getCategoryId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + task.getCategoryId()));
        checkMembership(category.getBoardId(), userId);

        Boolean isActive = task.getActive() != null ? task.getActive() : true;
        Task taskToSave = task.toBuilder().active(isActive).build();
        return taskRepository.save(taskToSave);
    }

    @Override
    public Task getTaskById(UUID id, UUID userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Task not found with id: " + id));
        Category category = categoryRepository.findById(task.getCategoryId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + task.getCategoryId()));
        checkMembership(category.getBoardId(), userId);
        return task;
    }

    @Override
    public List<Task> getAllTasks(UUID userId) {
        List<BoardUser> memberships = boardUserRepository.findAllByUserId(userId);
        List<UUID> memberBoardIds = memberships.stream().map(BoardUser::getBoardId).toList();
        if (memberBoardIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Category> categories = categoryRepository.findAllByBoardIdIn(memberBoardIds);
        if (categories.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<UUID> categoryIds = categories.stream().map(Category::getId).toList();
        return taskRepository.findAllByCategoryIdIn(categoryIds);
    }

    @Override
    @Transactional
    public void deleteTask(UUID id, UUID userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Task not found with id: " + id));
        Category category = categoryRepository.findById(task.getCategoryId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + task.getCategoryId()));
        checkMembership(category.getBoardId(), userId);

        transactionRepository.setTaskIdToNull(id);
        taskRepository.deleteById(id);
    }
}
