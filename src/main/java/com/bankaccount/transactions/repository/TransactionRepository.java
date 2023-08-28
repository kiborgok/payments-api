package com.bankaccount.transactions.repository;

import com.bankaccount.transactions.model.Account;
import com.bankaccount.transactions.model.Customer;
import com.bankaccount.transactions.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(UUID transactionId);
}
