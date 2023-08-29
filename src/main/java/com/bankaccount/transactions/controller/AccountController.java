package com.bankaccount.transactions.controller;

import com.bankaccount.transactions.dto.request.CustomerCreateAccountRequest;
import com.bankaccount.transactions.dto.request.AmountRequest;
import com.bankaccount.transactions.dto.request.TranferRequest;
import com.bankaccount.transactions.dto.response.AccountDto;
import com.bankaccount.transactions.dto.response.BalanceResponse;
import com.bankaccount.transactions.service.ApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Controller")
public class AccountController {
    private final ApplicationService service;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<?> createAccount(@RequestBody CustomerCreateAccountRequest accountRequest){
        logger.info("Create customer account request :: {}", accountRequest);
        return service.createCustomerWithAccount(accountRequest);
    }

    @GetMapping
    List<AccountDto> getAllAccounts(){
        return service.getAllAccounts();
    }

    @GetMapping("/{customerId}")
    AccountDto getAccountByCustomerId(@PathVariable("customerId") String customerId){
        return service.getAccountByCustomerId(customerId);
    }

    @PostMapping("/{accountNumber}/deposit")
    public BalanceResponse deposit(@PathVariable("accountNumber") Long accountNumber, @RequestBody AmountRequest depositRequest) {
        return service.deposit(accountNumber, depositRequest);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public BalanceResponse withdraw(@PathVariable("accountNumber") Long accountNumber, @RequestBody AmountRequest depositRequest) {
        return service.withdraw(accountNumber, depositRequest);
    }

    @PostMapping("/{accountNumber}/balance")
    public BalanceResponse balance(@PathVariable("accountNumber") Long accountNumber) {
        return service.getAccountBalance(accountNumber);
    }

    @PostMapping("/{fromAccountNumber}/transfer")
    public BalanceResponse transfer(@PathVariable("fromAccountNumber") Long accountNumber, @RequestBody TranferRequest tranferRequest) {
        return service.fundsTranfer(accountNumber, tranferRequest);
    }
}
