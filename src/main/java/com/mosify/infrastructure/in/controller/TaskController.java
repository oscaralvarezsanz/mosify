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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<WebTaskResponse> createTask(@RequestBody WebTaskRequest request) {
        Task task = taskWebConverter.toDomain(request);
        Task created = taskCreatePort.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskWebConverter.toWebResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebTaskResponse> getTaskById(@PathVariable UUID id) {
        Task task = taskGetByIdPort.getTaskById(id);
        return ResponseEntity.ok(taskWebConverter.toWebResponse(task));
    }

    @GetMapping
    public ResponseEntity<List<WebTaskResponse>> getAllTasks() {
        List<WebTaskResponse> responses = taskGetAllPort.getAllTasks().stream()
                .map(taskWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<WebTransactionResponse> executeTask(@PathVariable UUID id, @RequestParam UUID userId) {
        Transaction tx = taskExecutePort.executeTask(id, userId);
        return ResponseEntity.ok(transactionWebConverter.toWebResponse(tx));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskDeletePort.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
