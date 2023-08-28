package com.bankaccount.transactions.dto.request;

import lombok.Data;

@Data
public class TranferRequest {
    private long toAccountNumber;
    private double amount;
}
