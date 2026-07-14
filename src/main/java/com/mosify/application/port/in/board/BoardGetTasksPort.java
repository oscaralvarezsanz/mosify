package com.mosify.application.port.in.board;

import com.mosify.domain.model.Task;
import java.util.List;
import java.util.UUID;

public interface BoardGetTasksPort {
    List<Task> getBoardTasks(UUID boardId);
}
