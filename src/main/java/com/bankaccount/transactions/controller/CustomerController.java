package com.bankaccount.transactions.controller;

import com.bankaccount.transactions.dto.request.CustomerLoginRequest;
import com.bankaccount.transactions.dto.response.CustomerLoginResponse;
import com.bankaccount.transactions.dto.response.TransactionDto;
import com.bankaccount.transactions.model.Customer;
import com.bankaccount.transactions.service.ApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Controller")
public class CustomerController {
    private final ApplicationService service;

    public CustomerController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public CustomerLoginResponse login(@RequestBody CustomerLoginRequest loginRequest) {
        return service.login(loginRequest);
    }

    @GetMapping
    List<Customer> getAllCustomers(){
        return service.getAllCustomers();
    }

}
