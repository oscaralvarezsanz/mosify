package com.mosify.application.port.in.category;

import com.mosify.domain.model.Category;
import java.util.List;

public interface CategoryGetAllPort {
    List<Category> getAllCategories();
}
