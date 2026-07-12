package com.mosify.infrastructure.out.db.task.mapper;

import com.mosify.domain.model.Task;
import com.mosify.domain.model.TaskFrequency;
import com.mosify.domain.model.TaskType;
import com.mosify.infrastructure.out.db.model.TaskEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskEntityConverterTest {

    private final TaskEntityConverter converter = Mappers.getMapper(TaskEntityConverter.class);

    @Test
    public void shouldMapEntityToDomain() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        TaskEntity entity = TaskEntity.builder()
                .id(id)
                .title("Math exercise")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .frequency(TaskFrequency.DAILY)
                .pointsValue(50)
                .active(true)
                .build();

        Task domain = converter.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getTitle()).isEqualTo("Math exercise");
        assertThat(domain.getCategoryId()).isEqualTo(categoryId);
        assertThat(domain.getType()).isEqualTo(TaskType.RECURRENT);
        assertThat(domain.getFrequency()).isEqualTo(TaskFrequency.DAILY);
        assertThat(domain.getPointsValue()).isEqualTo(50);
        assertThat(domain.getActive()).isTrue();
    }

    @Test
    public void shouldMapDomainToEntity() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Task domain = Task.builder()
                .id(id)
                .title("Math exercise")
                .categoryId(categoryId)
                .type(TaskType.RECURRENT)
                .frequency(TaskFrequency.DAILY)
                .pointsValue(50)
                .active(true)
                .build();

        TaskEntity entity = converter.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getTitle()).isEqualTo("Math exercise");
        assertThat(entity.getCategoryId()).isEqualTo(categoryId);
        assertThat(entity.getType()).isEqualTo(TaskType.RECURRENT);
        assertThat(entity.getFrequency()).isEqualTo(TaskFrequency.DAILY);
        assertThat(entity.getPointsValue()).isEqualTo(50);
        assertThat(entity.getActive()).isTrue();
    }
}
