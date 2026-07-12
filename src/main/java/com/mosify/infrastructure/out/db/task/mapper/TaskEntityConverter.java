package com.mosify.infrastructure.out.db.task.mapper;

import com.mosify.domain.model.Task;
import com.mosify.infrastructure.out.db.model.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskEntityConverter {
    Task toDomain(TaskEntity entity);
    TaskEntity toEntity(Task domain);
}
