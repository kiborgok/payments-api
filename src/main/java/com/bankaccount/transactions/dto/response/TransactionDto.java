package com.bankaccount.transactions.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDto {
    private Long id;
    private UUID transactionId;
    private double amount;
    private LocalDateTime dateTime;
    private String transactionDescription;
}
