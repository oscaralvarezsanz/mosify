package com.mosify.infrastructure.out.db.user.mapper;

import com.mosify.domain.model.User;
import com.mosify.infrastructure.out.db.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityConverter {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);
}
