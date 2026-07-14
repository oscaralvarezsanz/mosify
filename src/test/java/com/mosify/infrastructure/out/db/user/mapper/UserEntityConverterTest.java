package com.mosify.infrastructure.out.db.user.mapper;

import com.mosify.domain.model.User;
import com.mosify.infrastructure.out.db.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class UserEntityConverterTest {

    private final UserEntityConverter converter = Mappers.getMapper(UserEntityConverter.class);

    @Test
    public void shouldMapEntityToDomain() {
        UUID id = UUID.randomUUID();
        UserEntity entity = UserEntity.builder()
                .id(id)
                .name("Oscar")
                .username("oscar")
                .password("password")
                .build();

        User domain = converter.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("Oscar");
        assertThat(domain.getUsername()).isEqualTo("oscar");
        assertThat(domain.getPassword()).isEqualTo("password");
    }

    @Test
    public void shouldMapDomainToEntity() {
        UUID id = UUID.randomUUID();
        User domain = User.builder()
                .id(id)
                .name("Oscar")
                .username("oscar")
                .password("password")
                .build();

        UserEntity entity = converter.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Oscar");
        assertThat(entity.getUsername()).isEqualTo("oscar");
        assertThat(entity.getPassword()).isEqualTo("password");
    }
}
