package com.example.booking.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    @Autowired
    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public List<Account> getAccount() {
        return this.repository.findAll();
    }

    public Account addNewAccount(Account account) {
        long epochTimeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC.of("+08:00"));
        account.setCreatedTime(epochTimeNow);
        return this.repository.save(account);
    }
}
