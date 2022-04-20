package com.study.transactions.service;

import com.study.transactions.model.Account;

public interface AccountService {
    void createAccount();
    Account getAccount(Long id, Boolean useLock);
    void depositOne(Long id, Boolean useLock);
    void withdrawOne(Long id, Boolean useLock);
    void resetAccount(Long id);
    String getStat();
    void resetStat();
}
