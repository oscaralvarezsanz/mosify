package com.mosify.application.port.out.transaction;

import com.mosify.domain.model.Transaction;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findAll();
    List<Transaction> findAllByTaskIdAndUserId(UUID taskId, UUID userId);
    void deleteAllByUserId(UUID userId);
    void setTaskIdToNull(UUID taskId);
    List<Transaction> findAllByUserIdIn(List<UUID> userIds);
}
