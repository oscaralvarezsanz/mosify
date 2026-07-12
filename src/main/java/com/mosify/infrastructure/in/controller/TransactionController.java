package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.WebTransactionResponse;
import com.mosify.application.port.in.transaction.TransactionGetAllPort;
import com.mosify.infrastructure.in.mapper.TransactionWebConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionGetAllPort transactionGetAllPort;
    private final TransactionWebConverter transactionWebConverter;

    public TransactionController(TransactionGetAllPort transactionGetAllPort,
                                 TransactionWebConverter transactionWebConverter) {
        this.transactionGetAllPort = transactionGetAllPort;
        this.transactionWebConverter = transactionWebConverter;
    }

    @GetMapping
    public ResponseEntity<List<WebTransactionResponse>> getAllTransactions() {
        List<WebTransactionResponse> responses = transactionGetAllPort.getAllTransactions().stream()
                .map(transactionWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
