package com.mosify.application.port.in.board;

import java.util.UUID;

public interface BoardDeletePort {
    void deleteBoard(UUID id, UUID userId);
}
