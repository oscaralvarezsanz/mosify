package com.mosify.infrastructure.out.db.user;

import com.mosify.infrastructure.out.db.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
}
