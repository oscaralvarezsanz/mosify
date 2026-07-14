package com.mosify.infrastructure.security;

import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CustomUserDetailsServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

    @Test
    public void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Oscar")
                .username("oscar")
                .password("encodedPassword")
                .build();
        when(userRepository.findByUsername("oscar")).thenReturn(Optional.of(user));

        // When
        UserDetails result = service.loadUserByUsername("oscar");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("oscar");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    public void loadUserByUsername_whenUserDoesNotExist_shouldThrowException() {
        // Given
        when(userRepository.findByUsername("oscar")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.loadUserByUsername("oscar"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
