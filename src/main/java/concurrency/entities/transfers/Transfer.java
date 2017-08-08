package concurrency.entities.transfers;

import concurrency.ThreadsIdGenerator;
import concurrency.entities.BankAccount;

import javax.naming.InsufficientResourcesException;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Transfer implements Callable<Boolean> {
    private Integer id;
    private final static int WAIT_SEC = 3;
    private final static String UNABLE_TO_GET_THE_LOG_ERR_MSG = "Unable to get the  lock.";
    //example of using thread local variable variable. Each thread has its own copy of this variable.
    private final static ThreadLocal<Integer> threadId = new ThreadLocal<>();
    private final BankAccount accountFrom;
    private final BankAccount accountTo;
    private int amount;


    public Transfer(Integer id, BankAccount accountFrom, BankAccount accountTo, int amount) {
        this.id = id;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    /**
     * This method will try to transfer money from one account to the other account.
     * If resources(account) is taken by another thread right now, it will wait for {@link #WAIT_SEC}  seconds and then try again.
     * If it still busy it will print an error message.
     *
     * @throws InsufficientResourcesException if there is not enough money on the first account
     */
    @Override
    public Boolean call() throws Exception {
        threadId.set(ThreadsIdGenerator.generateId());
        System.out.println("Got new thread id " + threadId.get());
        if (accountFrom.getBalance() < amount) {
            throw new InsufficientResourcesException();
        }
        //Synchronized access to resources. Possible deadlock while executing transfer(firstAcc, secondAcc..) transfer(secondAcc, firstAcc..)
        if (accountFrom.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
            System.out.println("Got lock for the  first. Id: " + id);
            try {
                if (accountTo.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                    try {
                        //random delay
                        Thread.sleep(ThreadLocalRandom.current().nextInt(500, 2000 + 1));
                        System.out.println("Got lock for the second. Id: " + id);
                        accountFrom.withdraw(amount);
                        //random delay
                        Thread.sleep(ThreadLocalRandom.current().nextInt(500, 4000 + 1));
                        accountTo.deposit(amount);
                        System.out.println("Transferred from " + accountFrom + " to " + accountTo + " " + amount + ". Id: " + id);
                        return true;
                    } finally {
                        accountTo.getLock().unlock();
                    }
                }

            } finally {
                accountFrom.getLock().unlock();
            }
        } else {
            System.out.println(UNABLE_TO_GET_THE_LOG_ERR_MSG + id);
            accountFrom.incrementFailCounter();
            accountTo.incrementFailCounter();
        }
        return false;
    }
}
