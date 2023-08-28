package com.bankaccount.transactions.dto.request;

import lombok.Data;

@Data
public class CustomerCreateAccountRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String customerId;
}
