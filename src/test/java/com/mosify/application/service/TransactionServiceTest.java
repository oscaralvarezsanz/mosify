package com.mosify.application.service;

import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final BoardUserRepository boardUserRepository = mock(BoardUserRepository.class);

    private final TransactionService service = new TransactionService(
            transactionRepository,
            taskRepository,
            categoryRepository,
            boardUserRepository
    );

    private UUID transactionId;
    private UUID userId;
    private UUID taskId;
    private UUID categoryId;
    private UUID boardId;

    private Transaction transaction;
    private Task task;
    private Category category;
    private BoardUser boardUser;

    @BeforeEach
    public void setUp() {
        transactionId = UUID.randomUUID();
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        boardId = UUID.randomUUID();

        transaction = Transaction.builder()
                .id(transactionId)
                .userId(userId)
                .taskId(taskId)
                .pointsAffected(50)
                .createdAt(LocalDateTime.now())
                .build();

        task = Task.builder()
                .id(taskId)
                .title("Study Study")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .pointsValue(50)
                .active(true)
                .build();

        category = Category.builder()
                .id(categoryId)
                .name("Default Category")
                .boardId(boardId)
                .build();

        boardUser = BoardUser.builder()
                .boardId(boardId)
                .userId(userId)
                .pointsBalance(100)
                .build();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(boardUserRepository.findByBoardIdAndUserId(boardId, userId)).thenReturn(Optional.of(boardUser));
    }

    @Test
    public void shouldUndoTransactionSuccessfullyAndDeductPoints() {
        service.undoTransaction(transactionId, userId);

        ArgumentCaptor<BoardUser> userCaptor = ArgumentCaptor.forClass(BoardUser.class);
        verify(boardUserRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPointsBalance()).isEqualTo(50);

        verify(transactionRepository).deleteById(transactionId);
    }

    @Test
    public void shouldCapPointsAtZeroWhenUndoingResultsInNegativeBalance() {
        Transaction hugeTransaction = transaction.toBuilder().pointsAffected(150).build();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(hugeTransaction));

        service.undoTransaction(transactionId, userId);

        ArgumentCaptor<BoardUser> userCaptor = ArgumentCaptor.forClass(BoardUser.class);
        verify(boardUserRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPointsBalance()).isEqualTo(0);

        verify(transactionRepository).deleteById(transactionId);
    }

    @Test
    public void shouldUndoNegativeTransactionAndAddPointsBack() {
        Transaction negativeTx = transaction.toBuilder().pointsAffected(-50).build();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(negativeTx));

        service.undoTransaction(transactionId, userId);

        ArgumentCaptor<BoardUser> userCaptor = ArgumentCaptor.forClass(BoardUser.class);
        verify(boardUserRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPointsBalance()).isEqualTo(150);

        verify(transactionRepository).deleteById(transactionId);
    }

    @Test
    public void shouldReactivateSingleUseTaskOnUndo() {
        Task singleUseTask = task.toBuilder()
                .type(TaskType.SINGLE_USE)
                .active(false)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(singleUseTask));

        service.undoTransaction(transactionId, userId);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getActive()).isTrue();

        verify(transactionRepository).deleteById(transactionId);
    }

    @Test
    public void shouldThrowNotFoundWhenTransactionDoesNotExist() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.undoTransaction(transactionId, userId))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("Transaction not found")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verifyNoInteractions(taskRepository, categoryRepository, boardUserRepository);
    }

    @Test
    public void shouldThrowForbiddenWhenCallerIsNotOwner() {
        UUID otherUser = UUID.randomUUID();

        assertThatThrownBy(() -> service.undoTransaction(transactionId, otherUser))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("Caller is not the owner")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);

        verifyNoInteractions(taskRepository, categoryRepository, boardUserRepository);
    }

    @Test
    public void shouldThrowNotFoundWhenTaskDeleted() {
        Transaction txWithoutTask = transaction.toBuilder().taskId(null).build();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(txWithoutTask));

        assertThatThrownBy(() -> service.undoTransaction(transactionId, userId))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("Task associated with transaction has been deleted")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verifyNoInteractions(taskRepository, categoryRepository, boardUserRepository);
    }
}
