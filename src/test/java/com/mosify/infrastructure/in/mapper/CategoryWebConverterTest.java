package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebCategoryRequest;
import com.mosify.api.model.WebCategoryResponse;
import com.mosify.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWebConverterTest {

    private final CategoryWebConverter converter = Mappers.getMapper(CategoryWebConverter.class);

    @Test
    public void shouldMapRequestToDomain() {
        UUID userId = UUID.randomUUID();
        WebCategoryRequest request = WebCategoryRequest.builder()
                .userId(userId)
                .name("Studies")
                .description("Academic tasks")
                .build();

        Category domain = converter.toDomain(request);

        assertThat(domain).isNotNull();
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getName()).isEqualTo("Studies");
        assertThat(domain.getDescription()).isEqualTo("Academic tasks");
    }

    @Test
    public void shouldMapDomainToResponse() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Category domain = Category.builder()
                .id(id)
                .userId(userId)
                .name("Studies")
                .description("Academic tasks")
                .build();

        WebCategoryResponse response = converter.toWebResponse(domain);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getName()).isEqualTo("Studies");
        assertThat(response.getDescription()).isEqualTo("Academic tasks");
    }
}
