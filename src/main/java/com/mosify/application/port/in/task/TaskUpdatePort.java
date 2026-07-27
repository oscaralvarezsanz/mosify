package com.mosify.application.port.in.task;

import com.mosify.domain.model.Task;
import java.util.UUID;

public interface TaskUpdatePort {
    Task updateTask(UUID id, Task task, UUID userId);
}
