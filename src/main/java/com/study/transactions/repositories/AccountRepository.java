package com.study.transactions.repositories;

import com.study.transactions.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

import javax.persistence.LockModeType;


public interface AccountRepository extends JpaRepository<Account, Long> {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findById(Long aLong);

    Optional<Account> findOneById(Long aLong);
}