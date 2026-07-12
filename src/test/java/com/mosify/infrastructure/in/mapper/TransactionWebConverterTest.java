package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebTransactionResponse;
import com.mosify.domain.model.Transaction;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionWebConverterTest {

    private final TransactionWebConverter converter = Mappers.getMapper(TransactionWebConverter.class);

    @Test
    public void shouldMapDomainToResponse() {
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

        WebTransactionResponse response = converter.toWebResponse(domain);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getTaskId()).isEqualTo(taskId);
        assertThat(response.getPointsAffected()).isEqualTo(50);
        assertThat(response.getCreatedAt()).isNotNull();
    }
}
