package concurrency.synchronizers;

import concurrency.entities.BankAccount;
import concurrency.entities.transfers.CountTransfer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class SynchronizingThreadsDemo {
    private static final int A_B_TRANSFERS_NUMB = 6;
    private static final int B_A_TRANSFERS_NUMB = 4;


    /**
     * Showcase of using {@link CountDownLatch} for running all threads simultaneously
     * and running transfers from second account to first account after all transfers from
     * first account to second account will be finished.
     */
    public static void main(String[] args) throws InterruptedException {
        //latch for starting threads executing simultaneously when they told to do so one time.
        CountDownLatch startLatch = new CountDownLatch(1);

        //latch for starting all secondAcc->firstAcc transfers after all firstAcc->secondAcc will be finished
        CountDownLatch baLatch = new CountDownLatch(A_B_TRANSFERS_NUMB);


        final BankAccount firstAcc = new BankAccount(10000);
        final BankAccount secondAcc = new BankAccount(20000);

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < A_B_TRANSFERS_NUMB; i++) {
            executorService.submit(new CountTransfer(i, firstAcc, secondAcc, ThreadLocalRandom.current().nextInt(400), startLatch, baLatch));
        }

        for (int i = A_B_TRANSFERS_NUMB; i < A_B_TRANSFERS_NUMB + B_A_TRANSFERS_NUMB; i++) {
            executorService.submit(new CountTransfer(i, firstAcc, secondAcc, ThreadLocalRandom.current().nextInt(400), baLatch, null));
        }
        // start of all firstAcc->secondAcc transfers
        startLatch.countDown();

        executorService.shutdown();
    }


}
