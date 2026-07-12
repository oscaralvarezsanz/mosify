package com.mosify.infrastructure.out.db.transaction;

import com.mosify.infrastructure.out.db.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByTaskIdAndUserId(UUID taskId, UUID userId);
}
