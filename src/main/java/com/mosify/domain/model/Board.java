package com.mosify.domain.model;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Board {
    UUID id;
    String name;
}
