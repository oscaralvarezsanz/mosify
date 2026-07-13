package com.mosify.application.service;

import com.mosify.application.port.in.user.UserCreatePort;
import com.mosify.application.port.in.user.UserDeletePort;
import com.mosify.application.port.in.user.UserGetAllPort;
import com.mosify.application.port.in.user.UserGetByIdPort;
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

    public UserService(UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       TaskRepository taskRepository,
                       TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public User createUser(User user) {
        Integer points = user.getPointsBalance() != null ? user.getPointsBalance() : 0;
        User userToSave = user.toBuilder().pointsBalance(points).build();
        return userRepository.save(userToSave);
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
        // Verify user exists
        userRepository.findById(id)
                .orElseThrow(() -> new MosifyException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + id));

        // Get all categories of this user
        List<Category> userCategories = categoryRepository.findAll().stream()
                .filter(c -> c.getUserId().equals(id))
                .toList();

        // Delete tasks in those categories
        if (!userCategories.isEmpty()) {
            List<UUID> categoryIds = userCategories.stream().map(Category::getId).toList();
            taskRepository.deleteAllByCategoryIdIn(categoryIds);
        }

        // Delete categories
        categoryRepository.deleteAllByUserId(id);

        // Delete transactions
        transactionRepository.deleteAllByUserId(id);

        // Delete user
        userRepository.deleteById(id);
    }
}
