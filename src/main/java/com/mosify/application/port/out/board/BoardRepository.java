package com.mosify.application.port.out.board;

import com.mosify.domain.model.Board;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository {
    Board save(Board board);
    Optional<Board> findById(UUID id);
    List<Board> findAll();
    void deleteById(UUID id);
}
