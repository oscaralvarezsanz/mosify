package com.mosify.application.service;

import com.mosify.application.port.in.task.TaskExecutePort;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskExecutionService implements TaskExecutePort {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public TaskExecutionService(TaskRepository taskRepository,
                                UserRepository userRepository,
                                CategoryRepository categoryRepository,
                                TransactionRepository transactionRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Transaction executeTask(UUID taskId, UUID userId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Task not found with id: " + taskId));

        if (!task.getActive()) {
            throw new MosifyException(ErrorCode.TASK_INACTIVE, "Task is inactive: " + taskId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        Category category = categoryRepository.findById(task.getCategoryId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + task.getCategoryId()));

        if (!category.getUserId().equals(userId)) {
            throw new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category does not belong to the user: " + userId);
        }

        verifyTaskRecurrency(taskId, userId, task);

        if (task.getPointsValue() < 0) {
            if (user.getPointsBalance() + task.getPointsValue() < 0) {
                throw new MosifyException(ErrorCode.INSUFFICIENT_BALANCE, "User has insufficient points balance: " + user.getPointsBalance());
            }
        }

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .taskId(taskId)
                .pointsAffected(task.getPointsValue())
                .createdAt(LocalDateTime.now())
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        updateUserBalance(task, user);

        if (task.getType() == TaskType.SINGLE_USE) {
            taskRepository.save(task.toBuilder().active(false).build());
        }

        return savedTransaction;
    }

    private void updateUserBalance(Task task, User user) {
        int newBalance = user.getPointsBalance() + task.getPointsValue();
        userRepository.save(user.toBuilder().pointsBalance(newBalance).build());
    }

    private void verifyTaskRecurrency(UUID taskId, UUID userId, Task task) {
        if (task.getType() == TaskType.RECURRENT && task.getFrequency() != null) {
            TaskFrequency.DateTimeRange range = task.getFrequency().calculateRange(LocalDateTime.now());

            List<Transaction> transactions = transactionRepository.findAllByTaskIdAndUserId(taskId, userId);
            boolean alreadyCompleted = transactions.stream()
                    .anyMatch(t -> !t.getCreatedAt().isBefore(range.start()) && !t.getCreatedAt().isAfter(range.end()));
            if (alreadyCompleted) {
                throw new MosifyException(ErrorCode.TASK_ALREADY_COMPLETED, "Task already completed in the current " + task.getFrequency().name().toLowerCase() + " period");
            }
        }
    }
}
