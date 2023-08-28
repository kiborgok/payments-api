package com.bankaccount.transactions.controller;

import com.bankaccount.transactions.dto.response.TransactionDto;
import com.bankaccount.transactions.service.ApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction Controller")
public class TransactionController {
    private final ApplicationService service;

    public TransactionController(ApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return service.getAllTransactions();
    }

    @GetMapping("{transactionId}")
    public TransactionDto getTransaction(@PathVariable("transactionId") UUID transactionId) {
        return service.getTransaction(transactionId);
    }

    @PostMapping("/{accountNumber}/miniStatements")
    List<TransactionDto> getMiniStatements(@PathVariable("accountNumber") long accountNumber){
        return service.getMiniStatements(accountNumber);
    }
}
