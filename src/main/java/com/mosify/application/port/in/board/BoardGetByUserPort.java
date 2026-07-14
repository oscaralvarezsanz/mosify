package com.mosify.application.port.in.board;

import com.mosify.domain.model.Board;
import java.util.List;
import java.util.UUID;

public interface BoardGetByUserPort {
    List<Board> getBoardsByUserId(UUID userId);
}
