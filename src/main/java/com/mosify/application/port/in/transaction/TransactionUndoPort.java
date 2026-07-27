package com.mosify.application.port.in.transaction;

import java.util.UUID;

public interface TransactionUndoPort {
    void undoTransaction(UUID transactionId, UUID callerUserId);
}
