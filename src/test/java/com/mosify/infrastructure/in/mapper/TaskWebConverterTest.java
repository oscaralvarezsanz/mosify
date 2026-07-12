package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebTaskRequest;
import com.mosify.api.model.WebTaskResponse;
import com.mosify.domain.model.Task;
import com.mosify.domain.model.TaskFrequency;
import com.mosify.domain.model.TaskType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskWebConverterTest {

    private final TaskWebConverter converter = Mappers.getMapper(TaskWebConverter.class);

    @Test
    public void shouldMapRequestToDomain() {
        UUID categoryId = UUID.randomUUID();
        WebTaskRequest request = WebTaskRequest.builder()
                .title("Math homework")
                .categoryId(categoryId)
                .type(WebTaskRequest.TypeEnum.RECURRENT)
                .frequency(WebTaskRequest.FrequencyEnum.DAILY)
                .pointsValue(50)
                .active(true)
                .build();

        Task domain = converter.toDomain(request);

        assertThat(domain).isNotNull();
        assertThat(domain.getTitle()).isEqualTo("Math homework");
        assertThat(domain.getCategoryId()).isEqualTo(categoryId);
        assertThat(domain.getType()).isEqualTo(TaskType.RECURRENT);
        assertThat(domain.getFrequency()).isEqualTo(TaskFrequency.DAILY);
        assertThat(domain.getPointsValue()).isEqualTo(50);
        assertThat(domain.getActive()).isTrue();
    }

    @Test
    public void shouldMapDomainToResponse() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Task domain = Task.builder()
                .id(id)
                .title("Math homework")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .frequency(TaskFrequency.DAILY)
                .pointsValue(50)
                .active(true)
                .build();

        WebTaskResponse response = converter.toWebResponse(domain);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getTitle()).isEqualTo("Math homework");
        assertThat(response.getCategoryId()).isEqualTo(categoryId);
        assertThat(response.getType()).isEqualTo(WebTaskResponse.TypeEnum.RECURRENT);
        assertThat(response.getFrequency()).isEqualTo(WebTaskResponse.FrequencyEnum.DAILY);
        assertThat(response.getPointsValue()).isEqualTo(50);
        assertThat(response.getActive()).isTrue();
    }
}
