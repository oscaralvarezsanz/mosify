package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebTaskRequest;
import com.mosify.api.model.WebTaskResponse;
import com.mosify.domain.model.Task;
import com.mosify.domain.model.TaskFrequency;
import com.mosify.domain.model.TaskType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskWebConverter {

    @Mapping(target = "type", source = "type")
    @Mapping(target = "frequency", source = "frequency")
    Task toDomain(WebTaskRequest request);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "frequency", source = "frequency")
    WebTaskResponse toWebResponse(Task domain);

    default TaskType mapType(WebTaskRequest.TypeEnum type) {
        if (type == null) return null;
        return TaskType.valueOf(type.name());
    }

    default TaskFrequency mapFrequency(WebTaskRequest.FrequencyEnum freq) {
        if (freq == null) return null;
        return TaskFrequency.valueOf(freq.name());
    }

    default WebTaskResponse.TypeEnum mapType(TaskType type) {
        if (type == null) return null;
        return WebTaskResponse.TypeEnum.valueOf(type.name());
    }

    default WebTaskResponse.FrequencyEnum mapFrequency(TaskFrequency freq) {
        if (freq == null) return null;
        return WebTaskResponse.FrequencyEnum.valueOf(freq.name());
    }
}
