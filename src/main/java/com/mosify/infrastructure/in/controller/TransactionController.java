package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.WebTransactionResponse;
import com.mosify.application.port.in.transaction.TransactionGetAllPort;
import com.mosify.application.port.in.transaction.TransactionUndoPort;
import com.mosify.infrastructure.in.mapper.TransactionWebConverter;
import com.mosify.infrastructure.security.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionGetAllPort transactionGetAllPort;
    private final TransactionUndoPort transactionUndoPort;
    private final TransactionWebConverter transactionWebConverter;

    public TransactionController(TransactionGetAllPort transactionGetAllPort,
                                 TransactionUndoPort transactionUndoPort,
                                 TransactionWebConverter transactionWebConverter) {
        this.transactionGetAllPort = transactionGetAllPort;
        this.transactionUndoPort = transactionUndoPort;
        this.transactionWebConverter = transactionWebConverter;
    }

    @GetMapping
    public ResponseEntity<List<WebTransactionResponse>> getAllTransactions() {
        List<WebTransactionResponse> responses = transactionGetAllPort.getAllTransactions().stream()
                .map(transactionWebConverter::toWebResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> undoTransaction(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null || securityUser.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID callerUserId = securityUser.getUser().getId();
        transactionUndoPort.undoTransaction(id, callerUserId);
        return ResponseEntity.noContent().build();
    }
}
