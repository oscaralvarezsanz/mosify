package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebCategoryRequest;
import com.mosify.api.model.WebCategoryResponse;
import com.mosify.domain.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryWebConverter {
    Category toDomain(WebCategoryRequest request);
    WebCategoryResponse toWebResponse(Category domain);
}
