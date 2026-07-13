package com.mosify.infrastructure.out.db.transaction;

import com.mosify.infrastructure.out.db.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByTaskIdAndUserId(UUID taskId, UUID userId);
    void deleteAllByUserId(UUID userId);

    @Modifying
    @Query("UPDATE TransactionEntity t SET t.taskId = null WHERE t.taskId = :taskId")
    void setTaskIdToNull(@Param("taskId") UUID taskId);
}
