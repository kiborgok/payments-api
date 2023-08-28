package com.bankaccount.transactions.dto.response;

import lombok.Data;

@Data
public class AccountDto {
    private Long id;
    private Long accountNumber;
    private double balance;
    private String customerId;
}
