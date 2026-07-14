package com.mosify.application.port.in.task;

import com.mosify.domain.model.Task;
import java.util.List;
import java.util.UUID;

public interface TaskGetAllPort {
    List<Task> getAllTasks(UUID userId);
}
