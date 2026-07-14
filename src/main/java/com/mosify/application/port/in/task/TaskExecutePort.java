package com.mosify.application.port.in.task;

import com.mosify.domain.model.Transaction;
import java.util.UUID;

public interface TaskExecutePort {
    Transaction executeTask(UUID taskId, UUID userId, UUID callerUserId);
}
