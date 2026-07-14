package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.WebTaskRequest;
import com.mosify.api.model.WebTaskResponse;
import com.mosify.api.model.WebTransactionResponse;
import com.mosify.application.port.in.task.TaskCreatePort;
import com.mosify.application.port.in.task.TaskDeletePort;
import com.mosify.application.port.in.task.TaskExecutePort;
import com.mosify.application.port.in.task.TaskGetAllPort;
import com.mosify.application.port.in.task.TaskGetByIdPort;
import com.mosify.domain.model.Task;
import com.mosify.domain.model.Transaction;
import com.mosify.infrastructure.in.mapper.TaskWebConverter;
import com.mosify.infrastructure.in.mapper.TransactionWebConverter;
import com.mosify.infrastructure.security.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskCreatePort taskCreatePort;
    private final TaskGetByIdPort taskGetByIdPort;
    private final TaskGetAllPort taskGetAllPort;
    private final TaskExecutePort taskExecutePort;
    private final TaskDeletePort taskDeletePort;
    private final TaskWebConverter taskWebConverter;
    private final TransactionWebConverter transactionWebConverter;

    public TaskController(TaskCreatePort taskCreatePort,
                          TaskGetByIdPort taskGetByIdPort,
                          TaskGetAllPort taskGetAllPort,
                          TaskExecutePort taskExecutePort,
                          TaskDeletePort taskDeletePort,
                          TaskWebConverter taskWebConverter,
                          TransactionWebConverter transactionWebConverter) {
        this.taskCreatePort = taskCreatePort;
        this.taskGetByIdPort = taskGetByIdPort;
        this.taskGetAllPort = taskGetAllPort;
        this.taskExecutePort = taskExecutePort;
        this.taskDeletePort = taskDeletePort;
        this.taskWebConverter = taskWebConverter;
        this.transactionWebConverter = transactionWebConverter;
    }

    @PostMapping
    public ResponseEntity<WebTaskResponse> createTask(
            @RequestBody WebTaskRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        Task task = taskWebConverter.toDomain(request);
        Task created = taskCreatePort.createTask(task, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskWebConverter.toWebResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebTaskResponse> getTaskById(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        Task task = taskGetByIdPort.getTaskById(id, userId);
        return ResponseEntity.ok(taskWebConverter.toWebResponse(task));
    }

    @GetMapping
    public ResponseEntity<List<WebTaskResponse>> getAllTasks(
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        List<WebTaskResponse> responses = taskGetAllPort.getAllTasks(userId).stream()
                .map(taskWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<WebTransactionResponse> executeTask(
            @PathVariable UUID id, 
            @RequestParam UUID userId,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID callerUserId = securityUser.getUser().getId();
        Transaction tx = taskExecutePort.executeTask(id, userId, callerUserId);
        return ResponseEntity.ok(transactionWebConverter.toWebResponse(tx));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = securityUser.getUser().getId();
        taskDeletePort.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
}
