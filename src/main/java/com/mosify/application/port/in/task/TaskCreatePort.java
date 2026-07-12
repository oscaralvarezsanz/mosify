package com.mosify.application.port.in.task;

import com.mosify.domain.model.Task;

public interface TaskCreatePort {
    Task createTask(Task task);
}
