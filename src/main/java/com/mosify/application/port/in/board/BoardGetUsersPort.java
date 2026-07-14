package com.mosify.application.port.in.board;

import com.mosify.domain.model.BoardUser;
import java.util.List;
import java.util.UUID;

public interface BoardGetUsersPort {
    List<BoardUser> getBoardUsers(UUID boardId);
}
