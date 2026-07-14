package com.mosify.application.port.in.category;

import com.mosify.domain.model.Category;
import java.util.List;
import java.util.UUID;

public interface CategoryGetAllPort {
    List<Category> getAllCategories(UUID userId);
}
