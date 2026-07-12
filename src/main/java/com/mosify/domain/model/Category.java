package com.mosify.domain.model;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Category {
    UUID id;
    UUID userId;
    String name;
    String description;
}
