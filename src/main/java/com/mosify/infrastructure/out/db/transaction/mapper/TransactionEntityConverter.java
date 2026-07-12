package com.mosify.infrastructure.out.db.transaction.mapper;

import com.mosify.domain.model.Transaction;
import com.mosify.infrastructure.out.db.model.TransactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionEntityConverter {
    Transaction toDomain(TransactionEntity entity);
    TransactionEntity toEntity(Transaction domain);
}
