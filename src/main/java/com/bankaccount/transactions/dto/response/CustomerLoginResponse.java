package com.bankaccount.transactions.dto.response;

import lombok.Data;

@Data
public class CustomerLoginResponse {
    private String message;
    private CustomerCreateAccountResponse customer;
    public CustomerLoginResponse(String message, CustomerCreateAccountResponse customer){
        this.message = message;
        this.customer =customer;
    }
}
