package com.mosify.infrastructure.security;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    public void generateToken_shouldCreateValidJwt() {
        // Given
        String username = "oscar";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo(username);
        assertThat(jwtUtil.validateToken(token, username)).isTrue();
    }

    @Test
    public void validateToken_withInvalidUsername_shouldReturnFalse() {
        // Given
        String username = "oscar";
        String token = jwtUtil.generateToken(username);

        // When / Then
        assertThat(jwtUtil.validateToken(token, "otherUser")).isFalse();
    }
}
