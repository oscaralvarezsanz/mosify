package com.mosify.application.service;

import com.mosify.application.port.in.transaction.TransactionGetAllPort;
import com.mosify.application.port.in.transaction.TransactionUndoPort;
import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService implements TransactionGetAllPort, TransactionUndoPort {

    private final TransactionRepository transactionRepository;
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final BoardUserRepository boardUserRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              TaskRepository taskRepository,
                              CategoryRepository categoryRepository,
                              BoardUserRepository boardUserRepository) {
        this.transactionRepository = transactionRepository;
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.boardUserRepository = boardUserRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional
    public void undoTransaction(UUID transactionId, UUID callerUserId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Transaction not found with id: " + transactionId));

        if (!callerUserId.equals(transaction.getUserId())) {
            throw new MosifyException(ErrorCode.FORBIDDEN, "Access denied. Caller is not the owner of this transaction.");
        }

        if (transaction.getTaskId() == null) {
            throw new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Task associated with transaction has been deleted");
        }

        Task task = taskRepository.findById(transaction.getTaskId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Task not found with id: " + transaction.getTaskId()));

        Category category = categoryRepository.findById(task.getCategoryId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found with id: " + task.getCategoryId()));

        BoardUser boardUser = boardUserRepository.findByBoardIdAndUserId(category.getBoardId(), transaction.getUserId())
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User is not a member of the board: " + category.getBoardId()));

        int newBalance = Math.max(boardUser.getPointsBalance() - transaction.getPointsAffected(), 0);

        boardUserRepository.save(boardUser.toBuilder().pointsBalance(newBalance).build());

        if (task.getType() == TaskType.SINGLE_USE && !task.getActive()) {
            taskRepository.save(task.toBuilder().active(true).build());
        }

        transactionRepository.deleteById(transactionId);
    }
}
