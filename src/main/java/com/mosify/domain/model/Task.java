package com.mosify.domain.model;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Task {
    UUID id;
    String title;
    UUID categoryId;
    TaskType type;
    TaskFrequency frequency;
    Integer pointsValue;
    Boolean active;
}
