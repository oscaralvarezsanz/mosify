package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.WebUserRequest;
import com.mosify.api.model.WebUserResponse;
import com.mosify.api.model.WebBoardResponse;
import com.mosify.application.port.in.user.UserCreatePort;
import com.mosify.application.port.in.user.UserDeletePort;
import com.mosify.application.port.in.user.UserGetAllPort;
import com.mosify.application.port.in.user.UserGetByIdPort;
import com.mosify.application.port.in.board.BoardGetByUserPort;
import com.mosify.domain.model.User;
import com.mosify.infrastructure.in.mapper.UserWebConverter;
import com.mosify.infrastructure.in.mapper.BoardWebConverter;
import com.mosify.infrastructure.security.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserCreatePort userCreatePort;
    private final UserGetByIdPort userGetByIdPort;
    private final UserGetAllPort userGetAllPort;
    private final UserDeletePort userDeletePort;
    private final BoardGetByUserPort boardGetByUserPort;
    private final UserWebConverter webConverter;
    private final BoardWebConverter boardWebConverter;

    public UserController(UserCreatePort userCreatePort,
                          UserGetByIdPort userGetByIdPort,
                          UserGetAllPort userGetAllPort,
                          UserDeletePort userDeletePort,
                          BoardGetByUserPort boardGetByUserPort,
                          UserWebConverter webConverter,
                          BoardWebConverter boardWebConverter) {
        this.userCreatePort = userCreatePort;
        this.userGetByIdPort = userGetByIdPort;
        this.userGetAllPort = userGetAllPort;
        this.userDeletePort = userDeletePort;
        this.boardGetByUserPort = boardGetByUserPort;
        this.webConverter = webConverter;
        this.boardWebConverter = boardWebConverter;
    }

    @PostMapping
    public ResponseEntity<WebUserResponse> createUser(@RequestBody WebUserRequest request) {
        User user = webConverter.toDomain(request);
        User created = userCreatePort.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(webConverter.toWebResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebUserResponse> getUserById(@PathVariable UUID id) {
        User user = userGetByIdPort.getUserById(id);
        return ResponseEntity.ok(webConverter.toWebResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<WebUserResponse>> getAllUsers() {
        List<WebUserResponse> responses = userGetAllPort.getAllUsers().stream()
                .map(webConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/boards")
    public ResponseEntity<List<WebBoardResponse>> getUserBoards(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null || !securityUser.getUser().getId().equals(id)) {
            throw new com.mosify.domain.exception.MosifyException(
                com.mosify.domain.exception.ErrorCode.FORBIDDEN,
                "Access denied. You can only view your own boards."
            );
        }
        List<WebBoardResponse> responses = boardGetByUserPort.getBoardsByUserId(id).stream()
                .map(boardWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userDeletePort.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
