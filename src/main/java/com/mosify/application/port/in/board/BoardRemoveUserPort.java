package com.mosify.application.port.in.board;

import java.util.UUID;

public interface BoardRemoveUserPort {
    void removeUserFromBoard(UUID boardId, UUID userId, UUID callerUserId);
}
