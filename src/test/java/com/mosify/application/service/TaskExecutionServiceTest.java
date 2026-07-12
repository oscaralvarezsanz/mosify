package com.mosify.application.service;

import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskExecutionServiceTest {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private TaskExecutionService service;

    @BeforeEach
    public void setUp() {
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        service = new TaskExecutionService(taskRepository, userRepository, categoryRepository, transactionRepository);
    }

    @Test
    public void shouldExecuteRewardTaskSuccessfully() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .title("Math Exercises")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .pointsValue(50)
                .active(true)
                .build();

        User user = User.builder()
                .id(userId)
                .name("Oscar")
                .pointsBalance(100)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .userId(userId)
                .name("Studies")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction tx = service.executeTask(taskId, userId);

        assertThat(tx).isNotNull();
        assertThat(tx.getPointsAffected()).isEqualTo(50);
        assertThat(tx.getUserId()).isEqualTo(userId);
        assertThat(tx.getTaskId()).isEqualTo(taskId);

        // Verify balance updated: 100 + 50 = 150
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPointsBalance()).isEqualTo(150);
    }

    @Test
    public void shouldExecuteCostTaskSuccessfullyWhenBalanceIsEnough() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .title("Buy skin in Fortnite")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .pointsValue(-80)
                .active(true)
                .build();

        User user = User.builder()
                .id(userId)
                .name("Oscar")
                .pointsBalance(100)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .userId(userId)
                .name("Entertainment")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction tx = service.executeTask(taskId, userId);

        assertThat(tx).isNotNull();
        assertThat(tx.getPointsAffected()).isEqualTo(-80);

        // Verify balance updated: 100 - 80 = 20
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPointsBalance()).isEqualTo(20);
    }

    @Test
    public void shouldThrowExceptionWhenExecutingCostTaskWithInsufficientBalance() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .title("Buy skin in Fortnite")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .pointsValue(-120)
                .active(true)
                .build();

        User user = User.builder()
                .id(userId)
                .name("Oscar")
                .pointsBalance(100)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .userId(userId)
                .name("Entertainment")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> service.executeTask(taskId, userId))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("User has insufficient points balance")
                .extracting(ex -> ((MosifyException) ex).getErrorCode())
                .isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);

        verify(transactionRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldDeactivateSingleUseTaskAfterExecution() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .title("Single assignment reward")
                .categoryId(categoryId)
                .type(TaskType.SINGLE_USE)
                .pointsValue(100)
                .active(true)
                .build();

        User user = User.builder()
                .id(userId)
                .name("Oscar")
                .pointsBalance(0)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .userId(userId)
                .name("Studies")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.executeTask(taskId, userId);

        // Verify task is saved as inactive
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getActive()).isFalse();
    }

    @Test
    public void shouldThrowExceptionWhenExecutingInactiveTask() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .title("Math Exercises")
                .active(false)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.executeTask(taskId, userId))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("Task is inactive")
                .extracting(ex -> ((MosifyException) ex).getErrorCode())
                .isEqualTo(ErrorCode.TASK_INACTIVE);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenExecutingRecurrentTaskTwiceInSamePeriod() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .title("Daily Math Exercises")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .frequency(TaskFrequency.DAILY)
                .pointsValue(50)
                .active(true)
                .build();

        User user = User.builder()
                .id(userId)
                .name("Oscar")
                .pointsBalance(100)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .userId(userId)
                .name("Studies")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .taskId(taskId)
                .userId(userId)
                .pointsAffected(50)
                .createdAt(LocalDateTime.now())
                .build();
        when(transactionRepository.findAllByTaskIdAndUserId(taskId, userId))
                .thenReturn(List.of(transaction));

        assertThatThrownBy(() -> service.executeTask(taskId, userId))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("Task already completed in the current daily period")
                .extracting(ex -> ((MosifyException) ex).getErrorCode())
                .isEqualTo(ErrorCode.TASK_ALREADY_COMPLETED);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void shouldAllowExecutingRecurrentTaskMultipleTimesIfNoFrequency() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .title("Free Play Exercises")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .frequency(null) // Infinite recurrent task
                .pointsValue(10)
                .active(true)
                .build();

        User user = User.builder()
                .id(userId)
                .name("Oscar")
                .pointsBalance(100)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .userId(userId)
                .name("Studies")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute first time
        Transaction tx1 = service.executeTask(taskId, userId);
        assertThat(tx1).isNotNull();

        // Execute second time immediately
        Transaction tx2 = service.executeTask(taskId, userId);
        assertThat(tx2).isNotNull();

        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }
}
