package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebUserRequest;
import com.mosify.api.model.WebUserResponse;
import com.mosify.domain.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class UserWebConverterTest {

    private final UserWebConverter converter = Mappers.getMapper(UserWebConverter.class);

    @Test
    public void shouldMapRequestToDomain() {
        WebUserRequest request = WebUserRequest.builder()
                .name("Oscar")
                .pointsBalance(150)
                .build();

        User domain = converter.toDomain(request);

        assertThat(domain).isNotNull();
        assertThat(domain.getName()).isEqualTo("Oscar");
        assertThat(domain.getPointsBalance()).isEqualTo(150);
    }

    @Test
    public void shouldMapDomainToResponse() {
        UUID id = UUID.randomUUID();
        User domain = User.builder()
                .id(id)
                .name("Oscar")
                .pointsBalance(150)
                .build();

        WebUserResponse response = converter.toWebResponse(domain);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Oscar");
        assertThat(response.getPointsBalance()).isEqualTo(150);
    }
}
