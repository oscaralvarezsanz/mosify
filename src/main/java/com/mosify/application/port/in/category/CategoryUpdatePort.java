package com.mosify.application.port.in.category;

import com.mosify.domain.model.Category;
import java.util.UUID;

public interface CategoryUpdatePort {
    Category updateCategory(UUID id, Category category, UUID userId);
}
