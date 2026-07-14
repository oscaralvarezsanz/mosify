package com.mosify.application.port.in.category;

import com.mosify.domain.model.Category;
import java.util.UUID;

public interface CategoryGetByIdPort {
    Category getCategoryById(UUID id, UUID userId);
}
