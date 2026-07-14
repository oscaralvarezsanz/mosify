package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.*;
import com.mosify.application.port.in.board.*;
import com.mosify.domain.model.Board;
import com.mosify.infrastructure.in.mapper.*;
import com.mosify.infrastructure.security.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardCreatePort boardCreatePort;
    private final BoardGetAllPort boardGetAllPort;
    private final BoardDeletePort boardDeletePort;
    private final BoardAddUserPort boardAddUserPort;
    private final BoardRemoveUserPort boardRemoveUserPort;
    private final BoardGetUsersPort boardGetUsersPort;
    private final BoardGetCategoriesPort boardGetCategoriesPort;
    private final BoardGetTasksPort boardGetTasksPort;
    private final BoardGetTransactionsPort boardGetTransactionsPort;

    private final BoardWebConverter boardWebConverter;
    private final CategoryWebConverter categoryWebConverter;
    private final TaskWebConverter taskWebConverter;
    private final TransactionWebConverter transactionWebConverter;

    public BoardController(BoardCreatePort boardCreatePort,
                           BoardGetAllPort boardGetAllPort,
                           BoardDeletePort boardDeletePort,
                           BoardAddUserPort boardAddUserPort,
                           BoardRemoveUserPort boardRemoveUserPort,
                           BoardGetUsersPort boardGetUsersPort,
                           BoardGetCategoriesPort boardGetCategoriesPort,
                           BoardGetTasksPort boardGetTasksPort,
                           BoardGetTransactionsPort boardGetTransactionsPort,
                           BoardWebConverter boardWebConverter,
                           CategoryWebConverter categoryWebConverter,
                           TaskWebConverter taskWebConverter,
                           TransactionWebConverter transactionWebConverter) {
        this.boardCreatePort = boardCreatePort;
        this.boardGetAllPort = boardGetAllPort;
        this.boardDeletePort = boardDeletePort;
        this.boardAddUserPort = boardAddUserPort;
        this.boardRemoveUserPort = boardRemoveUserPort;
        this.boardGetUsersPort = boardGetUsersPort;
        this.boardGetCategoriesPort = boardGetCategoriesPort;
        this.boardGetTasksPort = boardGetTasksPort;
        this.boardGetTransactionsPort = boardGetTransactionsPort;
        this.boardWebConverter = boardWebConverter;
        this.categoryWebConverter = categoryWebConverter;
        this.taskWebConverter = taskWebConverter;
        this.transactionWebConverter = transactionWebConverter;
    }

    @PostMapping
    public ResponseEntity<WebBoardResponse> createBoard(@RequestBody WebBoardRequest request) {
        Board board = boardWebConverter.toDomain(request);
        Board created = boardCreatePort.createBoard(board);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardWebConverter.toWebResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<WebBoardResponse>> getAllBoards() {
        List<WebBoardResponse> responses = boardGetAllPort.getAllBoards().stream()
                .map(boardWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        boardDeletePort.deleteBoard(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<WebBoardUserResponse>> getBoardUsers(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        List<WebBoardUserResponse> responses = boardGetUsersPort.getBoardUsers(id, userId).stream()
                .map(boardWebConverter::toWebUserResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/users/{userId}")
    public ResponseEntity<Void> addUserToBoard(
            @PathVariable UUID id, 
            @PathVariable UUID userId, 
            @RequestParam(value = "alias", required = false) String alias,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID callerUserId = securityUser.getUser().getId();
        boardAddUserPort.addUserToBoard(id, userId, alias, callerUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/users/{userId}")
    public ResponseEntity<Void> removeUserFromBoard(
            @PathVariable UUID id, 
            @PathVariable UUID userId,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID callerUserId = securityUser.getUser().getId();
        boardRemoveUserPort.removeUserFromBoard(id, userId, callerUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<List<WebCategoryResponse>> getBoardCategories(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        List<WebCategoryResponse> responses = boardGetCategoriesPort.getBoardCategories(id, userId).stream()
                .map(categoryWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<WebTaskResponse>> getBoardTasks(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        List<WebTaskResponse> responses = boardGetTasksPort.getBoardTasks(id, userId).stream()
                .map(taskWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<WebTransactionResponse>> getBoardTransactions(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        List<WebTransactionResponse> responses = boardGetTransactionsPort.getBoardTransactions(id, userId).stream()
                .map(transactionWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
