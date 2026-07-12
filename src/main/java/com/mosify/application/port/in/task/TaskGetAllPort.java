package com.mosify.application.port.in.task;

import com.mosify.domain.model.Task;
import java.util.List;

public interface TaskGetAllPort {
    List<Task> getAllTasks();
}
