package com.study.transactions.controllers;

import com.study.transactions.model.Account;
import com.study.transactions.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/deposit/{accountId}")
    public void deposit(@PathVariable Long accountId) {
        accountService.depositOne(accountId, true);
    }

    @GetMapping("/reset/{accountId}")
    public void reset(@PathVariable Long accountId) {
        accountService.resetAccount(accountId);
    }

    @GetMapping("/withdraw/{accountId}")
    public void withdraw(@PathVariable Long accountId) {
        accountService.withdrawOne(accountId, true);
    }

    @GetMapping("/account/{accountId}")
    public Account getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId, true);
    }

    @GetMapping("/account-stat")
    public String getAccountStat() {
        return accountService.getStat();
    }
}
