package com.mosify.application.port.in.category;

import java.util.UUID;

public interface CategoryDeletePort {
    void deleteCategory(UUID id, UUID userId);
}
