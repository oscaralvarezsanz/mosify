package com.mosify.application.port.in.transaction;

import com.mosify.domain.model.Transaction;
import java.util.List;

public interface TransactionGetAllPort {
    List<Transaction> getAllTransactions();
}
