package com.mosify.application.service;

import com.mosify.application.port.in.user.UserCreatePort;
import com.mosify.application.port.in.user.UserGetAllPort;
import com.mosify.application.port.in.user.UserGetByIdPort;
import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import com.mosify.domain.model.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserCreatePort, UserGetByIdPort, UserGetAllPort {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
