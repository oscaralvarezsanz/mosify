package com.mosify.application.port.in.board;

import com.mosify.domain.model.Transaction;
import java.util.List;
import java.util.UUID;

public interface BoardGetTransactionsPort {
    List<Transaction> getBoardTransactions(UUID boardId, UUID userId);
}
