package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebUserRequest;
import com.mosify.api.model.WebUserResponse;
import com.mosify.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserWebConverter {
    User toDomain(WebUserRequest request);
    WebUserResponse toWebResponse(User domain);
}
