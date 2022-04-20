package com.study.transactions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Stream;

import com.study.transactions.repositories.AccountRepository;
import com.study.transactions.service.AccountServiceImpl;

import static java.util.stream.Collectors.toList;

@SpringBootTest
@ActiveProfiles("h2-mem")
public class LockTest {
    @Autowired
    private AccountServiceImpl service;

    private static Long accountId = 0L;
    private static Boolean useLock = true;

    @BeforeEach
    public void clearDB(@Autowired AccountRepository repo) {
        repo.deleteAll();
        service.createAccount();
        accountId++;
    }

    private final int processors = Runtime.getRuntime().availableProcessors();

    private final CyclicBarrier barrier = new CyclicBarrier(processors);

    private final Runnable deposit = () -> {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 100; i++) {
            service.depositOne(accountId, useLock);
        }
    };

    private final Runnable withdraw = () -> {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 100; i++) {
            service.withdrawOne(accountId, useLock);
        }
    };

    @Test
    public void withLockShouldNotLostAnyUpdate() {
        useLock = true;

        final List<Thread> depositThreads = Stream.generate(() -> deposit)
            .limit(processors / 2)
            .map(Thread::new)
            .peek(Thread::start)
            .collect(toList());

        final List<Thread> withDrawThreads = Stream.generate(() -> withdraw)
            .limit(processors / 2)
            .map(Thread::new)
            .peek(Thread::start)
            .collect(toList());

        Stream.concat(depositThreads.stream(), withDrawThreads.stream())
            .forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


        Assertions.assertEquals(0, service.getAccount(accountId, true).getAmount());
    }

    @Test
    public void withoutLockShouldLostSomeUpdate() {
        useLock = false;

        final List<Thread> depositThreads = Stream.generate(() -> deposit)
            .limit(processors / 2)
            .map(Thread::new)
            .peek(Thread::start)
            .collect(toList());

        final List<Thread> withDrawThreads = Stream.generate(() -> withdraw)
            .limit(processors / 2)
            .map(Thread::new)
            .peek(Thread::start)
            .collect(toList());

        Stream.concat(depositThreads.stream(), withDrawThreads.stream())
            .forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


        Assertions.assertNotEquals(0, service.getAccount(accountId, false).getAmount());
    }
}