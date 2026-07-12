package com.mosify.infrastructure.out.db.category.mapper;

import com.mosify.domain.model.Category;
import com.mosify.infrastructure.out.db.model.CategoryEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryEntityConverterTest {

    private final CategoryEntityConverter converter = Mappers.getMapper(CategoryEntityConverter.class);

    @Test
    public void shouldMapEntityToDomain() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CategoryEntity entity = CategoryEntity.builder()
                .id(id)
                .userId(userId)
                .name("Studies")
                .description("Academic goals")
                .build();

        Category domain = converter.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getName()).isEqualTo("Studies");
        assertThat(domain.getDescription()).isEqualTo("Academic goals");
    }

    @Test
    public void shouldMapDomainToEntity() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Category domain = Category.builder()
                .id(id)
                .userId(userId)
                .name("Studies")
                .description("Academic goals")
                .build();

        CategoryEntity entity = converter.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getName()).isEqualTo("Studies");
        assertThat(entity.getDescription()).isEqualTo("Academic goals");
    }
}
