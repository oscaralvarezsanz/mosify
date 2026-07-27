package com.mosify.application.service;

import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.BoardUser;
import com.mosify.domain.model.Category;
import com.mosify.domain.model.Task;
import com.mosify.domain.model.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final BoardUserRepository boardUserRepository = mock(BoardUserRepository.class);

    private final TaskService service = new TaskService(
            taskRepository,
            categoryRepository,
            transactionRepository,
            boardUserRepository
    );

    private UUID taskId;
    private UUID categoryId;
    private UUID boardId;
    private UUID userId;
    private Task task;
    private Category category;
    private BoardUser boardUser;

    @BeforeEach
    public void setUp() {
        taskId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        boardId = UUID.randomUUID();
        userId = UUID.randomUUID();
        task = Task.builder()
                .id(taskId)
                .title("Old Title")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .pointsValue(10)
                .active(true)
                .build();
        category = Category.builder()
                .id(categoryId)
                .boardId(boardId)
                .userId(userId)
                .build();
        boardUser = BoardUser.builder()
                .boardId(boardId)
                .userId(userId)
                .build();
    }

    @Test
    public void shouldUpdateTaskSuccessfullyWhenMember() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(boardUserRepository.findAllByBoardId(boardId)).thenReturn(List.of(boardUser));
        when(boardUserRepository.findByBoardIdAndUserId(boardId, userId)).thenReturn(Optional.of(boardUser));

        Task updateRequest = Task.builder()
                .title("New Title")
                .type(TaskType.SINGLE_USE)
                .pointsValue(20)
                .active(false)
                .build();

        Task expectedSaved = task.toBuilder()
                .title("New Title")
                .type(TaskType.SINGLE_USE)
                .pointsValue(20)
                .active(false)
                .build();
        when(taskRepository.save(any(Task.class))).thenReturn(expectedSaved);

        Task result = service.updateTask(taskId, updateRequest, userId);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getType()).isEqualTo(TaskType.SINGLE_USE);
        assertThat(result.getPointsValue()).isEqualTo(20);
        assertThat(result.getActive()).isFalse();

        verify(taskRepository).save(argThat(tk -> 
                tk.getTitle().equals("New Title") &&
                tk.getType() == TaskType.SINGLE_USE &&
                tk.getPointsValue() == 20 &&
                !tk.getActive()
        ));
    }

    @Test
    public void shouldThrowNotFoundWhenTaskDoesNotExist() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Task updateRequest = Task.builder().title("New Title").build();

        assertThatThrownBy(() -> service.updateTask(taskId, updateRequest, userId))
                .isInstanceOf(MosifyException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(taskRepository, never()).save(any());
    }
}
