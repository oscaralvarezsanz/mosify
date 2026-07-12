package com.mosify.domain.model;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class User {
    UUID id;
    String name;
    Integer pointsBalance;
}
