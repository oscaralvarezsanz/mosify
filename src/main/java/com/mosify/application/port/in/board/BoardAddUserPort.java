package com.mosify.application.port.in.board;

import java.util.UUID;

public interface BoardAddUserPort {
    void addUserToBoard(UUID boardId, UUID userId, String alias);
}
