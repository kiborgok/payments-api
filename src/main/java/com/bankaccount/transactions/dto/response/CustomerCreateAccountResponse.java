package com.bankaccount.transactions.dto.response;

import lombok.Data;

@Data
public class CustomerCreateAccountResponse {
    private Long id;
    private String pin;
    private String firstName;
    private String lastName;
    private String email;
    private String customerId;
}
