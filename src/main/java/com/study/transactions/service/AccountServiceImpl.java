package com.study.transactions.service;

import com.study.transactions.model.Account;
import com.study.transactions.repositories.AccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
public class AccountServiceImpl implements AccountService {
    final AccountRepository accountRepository;

    private final AtomicInteger withdraws = new AtomicInteger();
    private final AtomicInteger deposits = new AtomicInteger();
    private final AtomicInteger amountVar = new AtomicInteger();

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void createAccount() {
        Account account = new Account(0);
        accountRepository.save(account);
    }

    @Override
    public Account getAccount(Long id, Boolean useLock) {
        if (useLock){
            return accountRepository.findById(id).get();
        } else {
            return accountRepository.findOneById(id).get();
        }
    }

    @Override
    @Transactional(isolation = SERIALIZABLE)
    public void depositOne(Long id, Boolean useLock) {
        Account account = getAccount(id, useLock);
        long amount = account.getAmount();
        account.setAmount(amount + 1);
        deposits.incrementAndGet();
        accountRepository.save(account);
        amountVar.incrementAndGet();
    }

    @Override
    @Transactional(isolation = SERIALIZABLE)
    public void withdrawOne(Long id, Boolean useLock) {
        Account account = getAccount(id, useLock);
        long amount = account.getAmount();
        account.setAmount(amount - 1);
        withdraws.incrementAndGet();
        accountRepository.save(account);
        amountVar.addAndGet(-1);
    }

    @Override
    @Transactional(isolation = SERIALIZABLE)
    public void resetAccount(Long id) {
        Account account = getAccount(id, true);
        account.setAmount(0);
        accountRepository.save(account);
    }

    @Override
    public String getStat() {
        return deposits.get() + " : " + withdraws.get() + " : " + amountVar.get();
    }

    @Override
    public void resetStat() {
        deposits.set(0);
        withdraws.set(0);
        amountVar.set(0);
    }
}
