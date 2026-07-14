package com.mosify.infrastructure.out.db.board.mapper;

import com.mosify.domain.model.BoardUser;
import com.mosify.infrastructure.out.db.model.BoardUserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardUserEntityConverter {
    BoardUser toDomain(BoardUserEntity entity);
    BoardUserEntity toEntity(BoardUser domain);
}
