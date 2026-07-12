package com.mosify.infrastructure.out.db.transaction.mapper;

import com.mosify.domain.model.Transaction;
import com.mosify.infrastructure.out.db.model.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionEntityConverterTest {

    private final TransactionEntityConverter converter = Mappers.getMapper(TransactionEntityConverter.class);

    @Test
    public void shouldMapEntityToDomain() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2026, 7, 5, 12, 0, 0);

        TransactionEntity entity = TransactionEntity.builder()
                .id(id)
                .userId(userId)
                .taskId(taskId)
                .pointsAffected(50)
                .createdAt(now)
                .build();

        Transaction domain = converter.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getTaskId()).isEqualTo(taskId);
        assertThat(domain.getPointsAffected()).isEqualTo(50);
        assertThat(domain.getCreatedAt()).isEqualTo(now);
    }

    @Test
    public void shouldMapDomainToEntity() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2026, 7, 5, 12, 0, 0);

        Transaction domain = Transaction.builder()
                .id(id)
                .userId(userId)
                .taskId(taskId)
                .pointsAffected(50)
                .createdAt(now)
                .build();

        TransactionEntity entity = converter.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getTaskId()).isEqualTo(taskId);
        assertThat(entity.getPointsAffected()).isEqualTo(50);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }
}
