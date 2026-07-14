package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebBoardRequest;
import com.mosify.api.model.WebBoardResponse;
import com.mosify.api.model.WebBoardUserResponse;
import com.mosify.domain.model.Board;
import com.mosify.domain.model.BoardUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardWebConverter {
    Board toDomain(WebBoardRequest request);
    WebBoardResponse toWebResponse(Board domain);
    WebBoardUserResponse toWebUserResponse(BoardUser domain);
}
