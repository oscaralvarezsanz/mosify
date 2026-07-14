package com.mosify.application.port.in.board;

import com.mosify.domain.model.Board;
import java.util.List;

public interface BoardGetAllPort {
    List<Board> getAllBoards();
}
