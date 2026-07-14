package com.mosify.application.service;

import com.mosify.application.port.in.task.TaskExecutePort;
import com.mosify.application.port.out.board.BoardUserRepository;
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
    private final BoardUserRepository boardUserRepository;

    public TaskExecutionService(TaskRepository taskRepository,
                                UserRepository userRepository,
                                CategoryRepository categoryRepository,
                                TransactionRepository transactionRepository,
                                BoardUserRepository boardUserRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.boardUserRepository = boardUserRepository;
    }

    @Override
    @Transactional
    public Transaction executeTask(UUID taskId, UUID userId, UUID callerUserId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Task not found with id: " + taskId));

        if (!task.getActive()) {
            throw new MosifyException(ErrorCode.TASK_INACTIVE, "Task is inactive: " + taskId);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        Category category = categoryRepository.findById(task.getCategoryId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + task.getCategoryId()));

        // Check if caller is member of board
        boardUserRepository.findByBoardIdAndUserId(category.getBoardId(), callerUserId)
                .orElseThrow(() -> new MosifyException(ErrorCode.FORBIDDEN, "Access denied. Caller is not a member of this board."));

        BoardUser boardUser = boardUserRepository.findByBoardIdAndUserId(category.getBoardId(), userId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User is not a member of the board: " + category.getBoardId()));

        verifyTaskRecurrency(taskId, userId, task);

        if (task.getPointsValue() < 0) {
            if (boardUser.getPointsBalance() + task.getPointsValue() < 0) {
                throw new MosifyException(ErrorCode.INSUFFICIENT_BALANCE, "User has insufficient points balance in this board: " + boardUser.getPointsBalance());
            }
        }

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .taskId(taskId)
                .pointsAffected(task.getPointsValue())
                .createdAt(LocalDateTime.now())
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        updateBoardUserBalance(task, boardUser);

        if (task.getType() == TaskType.SINGLE_USE) {
            taskRepository.save(task.toBuilder().active(false).build());
        }

        return savedTransaction;
    }

    private void updateBoardUserBalance(Task task, BoardUser boardUser) {
        int newBalance = boardUser.getPointsBalance() + task.getPointsValue();
        boardUserRepository.save(boardUser.toBuilder().pointsBalance(newBalance).build());
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
