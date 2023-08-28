package com.bankaccount.transactions.dto.request;

import lombok.Data;

@Data
public class CustomerLoginRequest {
    private String customerId;
    private String pin;
}
