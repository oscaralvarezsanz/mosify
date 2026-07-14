package com.mosify.application.service;

import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final BoardUserRepository boardUserRepository = mock(BoardUserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    
    private final UserService service = new UserService(
            userRepository,
            categoryRepository,
            taskRepository,
            transactionRepository,
            boardUserRepository,
            passwordEncoder
    );

    private User rawUser;

    @BeforeEach
    public void setUp() {
        rawUser = User.builder()
                .name("Oscar")
                .username("oscar")
                .password("plainPassword")
                .build();
    }

    @Test
    public void createUser_whenUsernameIsUnique_shouldEncodePasswordAndSave() {
        // Given
        when(userRepository.findByUsername("oscar")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        
        User savedUser = rawUser.toBuilder()
                .id(UUID.randomUUID())
                .password("encodedPassword")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = service.createUser(rawUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(argThat(user -> 
                user.getUsername().equals("oscar") && 
                user.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    public void createUser_whenUsernameAlreadyExists_shouldThrowException() {
        // Given
        when(userRepository.findByUsername("oscar")).thenReturn(Optional.of(rawUser));

        // When / Then
        assertThatThrownBy(() -> service.createUser(rawUser))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("Username already exists")
                .extracting(e -> ((MosifyException) e).getErrorCode())
                .isEqualTo(ErrorCode.BUSINESS_VALIDATION_ERROR);

        verify(userRepository, never()).save(any());
    }

    @Test
    public void getUserById_whenUserExists_shouldReturnUser() {
        UUID id = UUID.randomUUID();
        User expected = rawUser.toBuilder().id(id).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(expected));

        User result = service.getUserById(id);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void getUserById_whenUserDoesNotExist_shouldThrowException() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(id))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("User not found")
                .extracting(e -> ((MosifyException) e).getErrorCode())
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    public void getAllUsers_shouldReturnAllUsers() {
        List<User> list = List.of(rawUser);
        when(userRepository.findAll()).thenReturn(list);

        List<User> result = service.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(rawUser);
    }

    @Test
    public void deleteUser_whenUserExists_shouldDeleteAssociationsAndUser() {
        UUID id = UUID.randomUUID();
        User existingUser = rawUser.toBuilder().id(id).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        service.deleteUser(id);

        verify(transactionRepository).deleteAllByUserId(id);
        verify(boardUserRepository).deleteAllByUserId(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    public void deleteUser_whenUserDoesNotExist_shouldThrowException() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteUser(id))
                .isInstanceOf(MosifyException.class)
                .hasMessageContaining("User not found")
                .extracting(e -> ((MosifyException) e).getErrorCode())
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(userRepository, never()).deleteById(any());
    }
}
