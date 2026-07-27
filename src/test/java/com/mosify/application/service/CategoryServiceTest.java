package com.mosify.application.service;

import com.mosify.application.port.out.board.BoardRepository;
import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.BoardUser;
import com.mosify.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final BoardUserRepository boardUserRepository = mock(BoardUserRepository.class);

    private final CategoryService service = new CategoryService(
            categoryRepository,
            boardRepository,
            taskRepository,
            boardUserRepository
    );

    private UUID categoryId;
    private UUID boardId;
    private UUID userId;
    private Category category;
    private BoardUser boardUser;

    @BeforeEach
    public void setUp() {
        categoryId = UUID.randomUUID();
        boardId = UUID.randomUUID();
        userId = UUID.randomUUID();
        category = Category.builder()
                .id(categoryId)
                .boardId(boardId)
                .userId(userId)
                .name("Old Name")
                .description("Old Desc")
                .build();
        boardUser = BoardUser.builder()
                .boardId(boardId)
                .userId(userId)
                .build();
    }

    @Test
    public void shouldUpdateCategorySuccessfullyWhenMember() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(boardUserRepository.findAllByBoardId(boardId)).thenReturn(List.of(boardUser));
        when(boardUserRepository.findByBoardIdAndUserId(boardId, userId)).thenReturn(Optional.of(boardUser));

        Category updateRequest = Category.builder()
                .name("New Name")
                .description("New Desc")
                .build();

        Category expectedSaved = category.toBuilder()
                .name("New Name")
                .description("New Desc")
                .build();
        when(categoryRepository.save(any(Category.class))).thenReturn(expectedSaved);

        Category result = service.updateCategory(categoryId, updateRequest, userId);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        verify(categoryRepository).save(argThat(cat -> 
                cat.getName().equals("New Name") && cat.getDescription().equals("New Desc")
        ));
    }

    @Test
    public void shouldThrowNotFoundWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Category updateRequest = Category.builder().name("New Name").build();

        assertThatThrownBy(() -> service.updateCategory(categoryId, updateRequest, userId))
                .isInstanceOf(MosifyException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(categoryRepository, never()).save(any());
    }
}
