package com.mosify.application.port.in.category;

import com.mosify.domain.model.Category;

public interface CategoryCreatePort {
    Category createCategory(Category category);
}
