package com.mosify.infrastructure.out.db.category.mapper;

import com.mosify.domain.model.Category;
import com.mosify.infrastructure.out.db.model.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryEntityConverter {
    Category toDomain(CategoryEntity entity);
    CategoryEntity toEntity(Category domain);
}
