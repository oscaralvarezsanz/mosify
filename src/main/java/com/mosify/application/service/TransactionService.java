package com.mosify.application.service;

import com.mosify.application.port.in.transaction.TransactionGetAllPort;
import com.mosify.application.port.out.transaction.TransactionRepository;
import com.mosify.domain.model.Transaction;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService implements TransactionGetAllPort {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
