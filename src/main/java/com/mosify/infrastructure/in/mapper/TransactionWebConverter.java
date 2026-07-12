package com.mosify.infrastructure.in.mapper;

import com.mosify.api.model.WebTransactionResponse;
import com.mosify.domain.model.Transaction;
import org.mapstruct.Mapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface TransactionWebConverter {

    WebTransactionResponse toWebResponse(Transaction domain);

    default OffsetDateTime mapLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
