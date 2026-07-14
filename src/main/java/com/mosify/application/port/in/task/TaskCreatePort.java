package com.mosify.application.port.in.task;

import com.mosify.domain.model.Task;
import java.util.UUID;

public interface TaskCreatePort {
    Task createTask(Task task, UUID userId);
}
