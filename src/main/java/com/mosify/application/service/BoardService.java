package com.mosify.application.service;

import com.mosify.application.port.in.board.*;
import com.mosify.application.port.out.board.BoardRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService implements 
        BoardCreatePort, 
        BoardGetAllPort, 
        BoardDeletePort, 
        BoardAddUserPort, 
        BoardRemoveUserPort, 
        BoardGetUsersPort, 
        BoardGetCategoriesPort, 
        BoardGetTasksPort, 
        BoardGetTransactionsPort {

    private final BoardRepository boardRepository;
    private final BoardUserRepository boardUserRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final TransactionRepository transactionRepository;

    public BoardService(BoardRepository boardRepository, 
                        BoardUserRepository boardUserRepository, 
                        UserRepository userRepository, 
                        CategoryRepository categoryRepository, 
                        TaskRepository taskRepository, 
                        TransactionRepository transactionRepository) {
        this.boardRepository = boardRepository;
        this.boardUserRepository = boardUserRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    @Override
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteBoard(UUID id) {
        boardRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Board not found with id: " + id));

        List<Category> categories = categoryRepository.findAllByBoardId(id);
        if (!categories.isEmpty()) {
            List<UUID> categoryIds = categories.stream().map(Category::getId).toList();
            taskRepository.deleteAllByCategoryIdIn(categoryIds);
        }

        categoryRepository.deleteAllByBoardId(id);
        boardUserRepository.deleteAllByBoardId(id);
        boardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addUserToBoard(UUID boardId, UUID userId, String alias) {
        boardRepository.findById(boardId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "Board not found with id: " + boardId));
        userRepository.findById(userId)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        Optional<BoardUser> existing = boardUserRepository.findByBoardIdAndUserId(boardId, userId);
        if (existing.isEmpty()) {
            BoardUser boardUser = BoardUser.builder()
                    .boardId(boardId)
                    .userId(userId)
                    .alias(alias)
                    .pointsBalance(0)
                    .build();
            boardUserRepository.save(boardUser);
        } else {
            BoardUser updated = existing.get().toBuilder().alias(alias).build();
            boardUserRepository.save(updated);
        }
    }

    @Override
    @Transactional
    public void removeUserFromBoard(UUID boardId, UUID userId) {
        boardUserRepository.deleteByBoardIdAndUserId(boardId, userId);
    }

    @Override
    public List<BoardUser> getBoardUsers(UUID boardId) {
        List<BoardUser> memberships = boardUserRepository.findAllByBoardId(boardId);
        return memberships.stream()
                .map(m -> {
                    String name = userRepository.findById(m.getUserId())
                            .map(User::getName)
                            .orElse("Usuario");
                    return m.toBuilder().userName(name).build();
                })
                .toList();
    }

    @Override
    public List<Category> getBoardCategories(UUID boardId) {
        return categoryRepository.findAllByBoardId(boardId);
    }

    @Override
    public List<Task> getBoardTasks(UUID boardId) {
        List<Category> categories = categoryRepository.findAllByBoardId(boardId);
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }
        List<UUID> categoryIds = categories.stream().map(Category::getId).toList();
        return taskRepository.findAllByCategoryIdIn(categoryIds);
    }

    @Override
    public List<Transaction> getBoardTransactions(UUID boardId) {
        List<BoardUser> memberships = boardUserRepository.findAllByBoardId(boardId);
        if (memberships.isEmpty()) {
            return Collections.emptyList();
        }
        List<UUID> userIds = memberships.stream().map(BoardUser::getUserId).toList();
        return transactionRepository.findAllByUserIdIn(userIds);
    }
}
