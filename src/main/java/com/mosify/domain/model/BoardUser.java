package com.mosify.domain.model;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class BoardUser {
    UUID id;
    UUID boardId;
    UUID userId;
    String alias;
    Integer pointsBalance;
    String userName;
}
