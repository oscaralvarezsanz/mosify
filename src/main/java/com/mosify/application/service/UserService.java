package com.mosify.application.service;

import com.mosify.application.port.in.user.UserCreatePort;
import com.mosify.application.port.in.user.UserDeletePort;
import com.mosify.application.port.in.user.UserGetAllPort;
import com.mosify.application.port.in.user.UserGetByIdPort;
import com.mosify.application.port.out.board.BoardUserRepository;
import com.mosify.application.port.out.category.CategoryRepository;
import com.mosify.application.port.out.task.TaskRepository;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.Category;
import com.mosify.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserCreatePort, UserGetByIdPort, UserGetAllPort, UserDeletePort {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final TransactionRepository transactionRepository;
    private final BoardUserRepository boardUserRepository;

    public UserService(UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       TaskRepository taskRepository,
                       TransactionRepository transactionRepository,
                       BoardUserRepository boardUserRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.transactionRepository = transactionRepository;
        this.boardUserRepository = boardUserRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + id));

        transactionRepository.deleteAllByUserId(id);
        boardUserRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }
}
