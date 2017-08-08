package concurrency.entities.transfers;

import concurrency.entities.BankAccount;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class CountTransfer implements Callable<Boolean> {
    //usage of composition
    private Transfer transfer;

    private Integer id;

    private static final String WAITING_MSG = "Waiting to start. Id: ";
    private static final String COUNTING_DOWN_MSG = "Counting Down. Id: ";


    //    private final BankAccount accountFrom;
    //    private final BankAccount accountTo;
    //    int amount;
    private CountDownLatch startLatch;
    private CountDownLatch endLatch;


    public CountTransfer(Integer id, BankAccount accountFrom, BankAccount accountTo, int amount, CountDownLatch startLatch, CountDownLatch endLatch) {
        this.id = id;
        //        this.accountFrom = accountFrom;
        //        this.accountTo = accountTo;
        //        this.amount = amount;
        transfer = new Transfer(id, accountFrom, accountTo, amount);
        this.startLatch = startLatch;
        this.endLatch = endLatch;

    }

    public Integer getId() {
        return id;
    }

    /**
     * When this method executes it will notify startLatch that this thread awaits it. Then it will execute
     * actions and notify decrement count of the endLatch.
     */
    public Boolean call() throws Exception {
        if (startLatch != null) {
            startLatch.await();
            System.out.println(WAITING_MSG + id);
        }
        Boolean result = transfer.call();
        if (endLatch != null) {
            endLatch.countDown();
            System.out.println(COUNTING_DOWN_MSG + id);
        }
        return result;

    }
}
