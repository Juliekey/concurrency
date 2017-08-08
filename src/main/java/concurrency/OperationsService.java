package concurrency;

import concurrency.entities.BankAccount;
import concurrency.entities.transfers.Transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class OperationsService {
    private static final int NUMBER_OF_PROCESSES = 10;
    private static final int MAX_NUMB_OF_THREADS = 3;
    private static final int AWAIT_TERMINATION_SEC = 60;
    private static final int INITIAL_DELAY_SEC = 2;
    private static final int PERIOD_SEC = 1;

    /**
     * Example of using different types of {@link ExecutorService}. {@link ExecutorService} is used for
     * executing tasks for transferring money between two banking accounts. At the same
     * time {@link ScheduledExecutorService} is used for alerting evey {@link #PERIOD_SEC}
     * about fail rate in operations.
     *
     * @see Transfer
     * @see BankAccount
     */
    public static void main(String[] args) throws InterruptedException {
        final BankAccount firstAcc = new BankAccount(1000);
        final BankAccount secondAcc = new BankAccount(2000);
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_NUMB_OF_THREADS);
        List<Transfer> transfers = new ArrayList<>();
        ScheduledExecutorService monitoringExecutorService = createFailRateNotificationExecutorService(firstAcc);

        for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
            transfers.add(new Transfer(i, firstAcc, secondAcc, ThreadLocalRandom.current().nextInt(400)));
        }
        List<Future<Boolean>> results = executorService.invokeAll(transfers);
        executorService.shutdown();
        executorService.awaitTermination(AWAIT_TERMINATION_SEC, TimeUnit.SECONDS);
        monitoringExecutorService.shutdownNow();
        printFutureResults(results, transfers);
    }

    private static ScheduledExecutorService createFailRateNotificationExecutorService(final BankAccount account) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(
                () -> System.out.println("Failed transfers in first bank account: " + account.getFailCounter()),
                INITIAL_DELAY_SEC,
                PERIOD_SEC,
                TimeUnit.SECONDS);
        return scheduledExecutorService;
    }

    private static void printFutureResults(List<Future<Boolean>> results, List<Transfer> transfers) throws InterruptedException {
        for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
            {
                try {
                    System.out.println("Transfer #" + transfers.get(i).getId() + " got result " + results.get(i).get());
                } catch (ExecutionException e) {
                    System.out.println("Transfer #" + transfers.get(i).getId() + " got  " + e.getMessage());
                }
            }
        }
    }

}
