package com.mosify.application.port.out.board;

import com.mosify.domain.model.BoardUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardUserRepository {
    BoardUser save(BoardUser boardUser);
    List<BoardUser> findAllByBoardId(UUID boardId);
    List<BoardUser> findAllByUserId(UUID userId);
    Optional<BoardUser> findByBoardIdAndUserId(UUID boardId, UUID userId);
    void deleteById(UUID id);
    void deleteByBoardIdAndUserId(UUID boardId, UUID userId);
    void deleteAllByBoardId(UUID boardId);
    void deleteAllByUserId(UUID userId);
}
