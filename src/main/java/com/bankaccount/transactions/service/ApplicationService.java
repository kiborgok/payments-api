package com.bankaccount.transactions.service;

import com.bankaccount.transactions.dto.request.CustomerCreateAccountRequest;
import com.bankaccount.transactions.dto.request.CustomerLoginRequest;
import com.bankaccount.transactions.dto.request.AmountRequest;
import com.bankaccount.transactions.dto.request.TranferRequest;
import com.bankaccount.transactions.dto.response.*;
import com.bankaccount.transactions.exceptions.NotFoundException;
import com.bankaccount.transactions.model.Account;
import com.bankaccount.transactions.model.Customer;
import com.bankaccount.transactions.model.Transaction;
import com.bankaccount.transactions.repository.AccountRepository;
import com.bankaccount.transactions.repository.CustomerRepository;
import com.bankaccount.transactions.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ApplicationService(CustomerRepository customerRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public ResponseEntity<?> createCustomerWithAccount(CustomerCreateAccountRequest registrationRequest) {
        //Check if customer exists
        Optional<Customer> customerPresent = customerRepository.findByCustomerId(registrationRequest.getCustomerId());
        if(customerPresent.isPresent()){
            return new ResponseEntity<>("Customer with ID " + registrationRequest.getCustomerId() + " already exists", HttpStatus.OK);
        }

        //Create new customer and account
        Customer customer = new Customer();
        Random random = new Random();

        // Generate a random 4-digit number
        int randomPin = random.nextInt(9000) + 1000;

        customer.setPin(String.valueOf(randomPin));
        customer.setFirstName(registrationRequest.getFirstName());
        customer.setLastName(registrationRequest.getLastName());
        customer.setEmail(registrationRequest.getEmail());
        customer.setCustomerId(registrationRequest.getCustomerId());

        Customer newCustomer = customerRepository.save(customer);

        // Create an account for the customer
        Account account = new Account();
        // Generate a random 9-digit number
        long accountNumber = (long) (random.nextDouble() * 900000000L) + 100000000L;
        account.setBalance(0);
        account.setAccountNumber(accountNumber);
        account.setCustomer(newCustomer);
        accountRepository.save(account);

        return new ResponseEntity<>(newCustomer, HttpStatus.OK);
    }

    public CustomerLoginResponse login(CustomerLoginRequest loginRequest){
        Optional<Customer> customer = customerRepository.findByCustomerId(loginRequest.getCustomerId());
        if(customer.isPresent()){
            if (customer.get().getPin().equals(loginRequest.getPin())){
                CustomerCreateAccountResponse customerResponse = convertToCustomerDto(customer.get());
                return new CustomerLoginResponse("Login successful", customerResponse);
            }
            return new CustomerLoginResponse("Invalid customerId/pin", null);
        }
        return new CustomerLoginResponse("Invalid customerId/pin", null);
    }


    public List<AccountDto> getAllAccounts(){
        List<Account> accounts = accountRepository.findAll();

        List<AccountDto> accountDTOs = accounts.stream()
                .map(account -> convertToAccountDTO(account))
                .collect(Collectors.toList());

        return accountDTOs;
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public AccountDto getAccountByCustomerId(String customerId){
        //Check if customer exists
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Customer with customer ID: " + customerId + " not found"));

        Account account = accountRepository.findByCustomer(customer)
                .orElseThrow(() -> new NotFoundException("Account not found for customer ID: " + customerId));

        AccountDto accountDto = convertToAccountDTO(account);

        return accountDto;
    }

    public List<TransactionDto> getMiniStatements(long accountNumber){
        //Check if account exists
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account number: " + accountNumber + " not found"));

         List<TransactionDto> transactionDtos = account.getTransactions()
                 .stream()
                 .sorted(Comparator.comparing(Transaction::getTransactionDateTime).reversed())
                 .limit(10)
                 .map(transaction -> convertToTransactionDTO(transaction))
                 .collect(Collectors.toList());

        return transactionDtos;
    }

    private AccountDto convertToAccountDTO(Account account) {
        AccountDto accountDTO = new AccountDto();
        accountDTO.setId(account.getId());
        accountDTO.setAccountNumber(account.getAccountNumber());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setCustomerId(account.getCustomer().getCustomerId());
        // Set other fields as needed
        return accountDTO;
    }

    private CustomerCreateAccountResponse convertToCustomerDto(Customer customer) {
        CustomerCreateAccountResponse customerResponseDTO = new CustomerCreateAccountResponse();
        customerResponseDTO.setId(customer.getId());
        customerResponseDTO.setPin(customer.getPin());
        customerResponseDTO.setFirstName(customer.getFirstName());
        customerResponseDTO.setLastName(customer.getLastName());
        customerResponseDTO.setEmail(customer.getEmail());
        customerResponseDTO.setCustomerId(customer.getCustomerId());
        return customerResponseDTO;
    }

    private TransactionDto convertToTransactionDTO(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setTransactionId(transaction.getTransactionId());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setDateTime(transaction.getTransactionDateTime());
        transactionDto.setTransactionDescription(transaction.getTransactionDescription());
        return transactionDto;
    }

    @Transactional
    public BalanceResponse deposit(Long accountNumber, AmountRequest depositRequest){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found with acount number: " + accountNumber));
        double amount = Double.valueOf(depositRequest.getAmount());
        double currentBalance = account.getBalance();
        double updatedBalance = currentBalance + amount;
        account.setBalance(updatedBalance);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAccount(account);
        transaction.setTransactionDescription("Deposit");

        transactionRepository.save(transaction);
        accountRepository.save(account);

        return new BalanceResponse(updatedBalance);
    }

    @Transactional
    public BalanceResponse withdraw(Long accountNumber, AmountRequest depositRequest){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found with acount number: " + accountNumber));
        double amount = Double.valueOf(depositRequest.getAmount());
        double currentBalance = account.getBalance();
        if (currentBalance < amount){
            return new BalanceResponse(currentBalance);
        }

        double updatedBalance = currentBalance - amount;
        account.setBalance(updatedBalance);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAccount(account);
        transaction.setTransactionDescription("Withdrawal");

        transactionRepository.save(transaction);
        accountRepository.save(account);

        return new BalanceResponse(updatedBalance);
    }

    @Transactional
    public BalanceResponse fundsTranfer(Long fromAccountNumber,TranferRequest tranferRequest){
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found with acount number: " + fromAccountNumber));
        log.info("Transfer request :: {}", tranferRequest);
        log.info("To account number :: {}", tranferRequest.getToAccountNumber());
        Account toAccount = accountRepository.findByAccountNumber(tranferRequest.getToAccountNumber())
                .orElseThrow(() -> new NotFoundException("Destination account number: " + tranferRequest.getToAccountNumber() + " not found"));

        double amount = Double.valueOf(tranferRequest.getAmount());
        double fromAccountBalance = fromAccount.getBalance();
        double toAccountBalance = toAccount.getBalance();
        if (fromAccountBalance < amount){
            return new BalanceResponse(fromAccountBalance);
        }

        double fromAccountUpdatedBalance = fromAccountBalance - amount;
        double toAccountUpdatedBalance = toAccountBalance + amount;
        fromAccount.setBalance(fromAccountUpdatedBalance);
        toAccount.setBalance(toAccountUpdatedBalance);

        Transaction fromAccountTransaction = new Transaction();
        fromAccountTransaction.setAmount(amount);
        fromAccountTransaction.setTransactionId(UUID.randomUUID());
        fromAccountTransaction.setAccount(fromAccount);
        fromAccountTransaction.setTransactionDescription("Transfer to " + toAccount.getAccountNumber() + " account");

        Transaction toAccountTransaction = new Transaction();
        toAccountTransaction.setAmount(amount);
        toAccountTransaction.setTransactionId(UUID.randomUUID());
        toAccountTransaction.setAccount(toAccount);
        toAccountTransaction.setTransactionDescription("Receive from " + fromAccount.getAccountNumber() + " account");

        transactionRepository.save(fromAccountTransaction);
        transactionRepository.save(toAccountTransaction);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return new BalanceResponse(fromAccountUpdatedBalance);
    }

    public BalanceResponse getAccountBalance(long accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found with acount number: " + accountNumber));
        double accountBalance = account.getBalance();
        return new BalanceResponse(accountBalance);
    }



    public List<TransactionDto> getAllTransactions(){
        List<Transaction> transactions = transactionRepository.findAll();

        List<TransactionDto> transactionDtos = transactions.stream()
                .map(transaction -> convertToTransactionDTO(transaction))
                .collect(Collectors.toList());

        return transactionDtos;
    }

    public TransactionDto getTransaction(UUID transactionId){
        //Check if transaction exists
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction with transaction ID: " + transactionId + " not found"));

        TransactionDto transactionDto = convertToTransactionDTO(transaction);

        return transactionDto;
    }
}
