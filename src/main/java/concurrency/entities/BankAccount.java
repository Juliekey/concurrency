package concurrency.entities;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bank account entity
 */
public class BankAccount {
    private int balance;
    private Lock lock;
    private AtomicInteger failCounter;

    public BankAccount(int balance) {
        this.balance = balance;
        lock = new ReentrantLock();
        failCounter = new AtomicInteger();
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public int getBalance() {
        return balance;
    }

    public Lock getLock() {
        return lock;
    }

    public int incrementFailCounter() {
        return failCounter.incrementAndGet();
    }

    public int getFailCounter() {
        return failCounter.intValue();
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "balance=" + balance +
                '}';
    }

}
