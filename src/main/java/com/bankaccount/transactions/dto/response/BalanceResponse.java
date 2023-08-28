package com.bankaccount.transactions.dto.response;

import lombok.Data;

@Data
public class BalanceResponse {
    private double currentBalance;
    public BalanceResponse(double currentBalance){
        this.currentBalance = currentBalance;
    }
}
