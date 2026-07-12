package com.mosify.infrastructure.out.db.transaction;

import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.domain.model.Transaction;
import com.mosify.infrastructure.out.db.transaction.mapper.TransactionEntityConverter;
import com.mosify.infrastructure.out.db.model.TransactionEntity;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionPersistenceAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionEntityConverter entityConverter;

    public TransactionPersistenceAdapter(TransactionJpaRepository jpaRepository, TransactionEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = entityConverter.toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return entityConverter.toDomain(saved);
    }

    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAll().stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByTaskIdAndUserId(UUID taskId, UUID userId) {
        return jpaRepository.findAllByTaskIdAndUserId(taskId, userId).stream()
                .map(entityConverter::toDomain)
                .toList();
    }
}
