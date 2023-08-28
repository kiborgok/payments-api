package com.bankaccount.transactions.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String pin;
    private String firstName;
    private String lastName;
    private String email;
    private String customerId;
}
