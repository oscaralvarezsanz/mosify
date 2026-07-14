package com.mosify.infrastructure.out.db.board.mapper;

import com.mosify.domain.model.Board;
import com.mosify.infrastructure.out.db.model.BoardEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardEntityConverter {
    Board toDomain(BoardEntity entity);
    BoardEntity toEntity(Board domain);
}
