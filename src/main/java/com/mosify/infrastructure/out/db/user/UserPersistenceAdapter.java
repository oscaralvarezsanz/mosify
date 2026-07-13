package com.mosify.infrastructure.out.db.user;

import com.mosify.application.port.out.user.UserRepository;
import com.mosify.domain.model.User;
import com.mosify.infrastructure.out.db.user.mapper.UserEntityConverter;
import com.mosify.infrastructure.out.db.model.UserEntity;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserEntityConverter entityConverter;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository, UserEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public User save(User user) {
        UserEntity entityToSave = entityConverter.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entityToSave);
        return entityConverter.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(entityConverter::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
