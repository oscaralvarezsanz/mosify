package com.mosify.application.service;

import com.mosify.application.port.out.board.BoardRepository;
import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.BoardUser;
import com.mosify.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BoardServiceTest {

    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final BoardUserRepository boardUserRepository = mock(BoardUserRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);

    private final BoardService service = new BoardService(
            boardRepository,
            boardUserRepository,
            userRepository,
            categoryRepository,
            taskRepository,
            transactionRepository
    );

    private UUID boardId;
    private UUID userId;
    private BoardUser boardUser;

    @BeforeEach
    public void setUp() {
        boardId = UUID.randomUUID();
        userId = UUID.randomUUID();
        boardUser = BoardUser.builder()
                .boardId(boardId)
                .userId(userId)
                .pointsBalance(100)
                .build();
    }

    @Test
    public void shouldGetBoardTransactionsSuccessfullyWhenMember() {
        when(boardUserRepository.findAllByBoardId(boardId)).thenReturn(List.of(boardUser));
        when(boardUserRepository.findByBoardIdAndUserId(boardId, userId)).thenReturn(Optional.of(boardUser));

        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pointsAffected(10)
                .build();
        when(transactionRepository.findAllByBoardId(boardId)).thenReturn(List.of(tx));

        List<Transaction> result = service.getBoardTransactions(boardId, userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(tx.getId());
        verify(transactionRepository).findAllByBoardId(boardId);
        verify(transactionRepository, never()).findAllByUserIdIn(any());
    }

    @Test
    public void shouldThrowForbiddenWhenNotMemberOfBoard() {
        when(boardUserRepository.findAllByBoardId(boardId)).thenReturn(List.of(boardUser));
        when(boardUserRepository.findByBoardIdAndUserId(boardId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBoardTransactions(boardId, userId))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("Access denied")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);

        verifyNoInteractions(transactionRepository);
    }
}
