package com.mosify.domain.model;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Transaction {
    UUID id;
    UUID userId;
    UUID taskId;
    Integer pointsAffected;
    LocalDateTime createdAt;
}
