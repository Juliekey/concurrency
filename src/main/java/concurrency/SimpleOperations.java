package concurrency;

import concurrency.entities.BankAccount;

import javax.naming.InsufficientResourcesException;
import java.util.concurrent.TimeUnit;

/**
 * This class represents business case of transferring money between two accounts.
 * This case implemented using{@link Thread} class.
 *
 * @see #transfer(BankAccount, BankAccount, int)
 * @see #main(String[])
 */
public class SimpleOperations {
    private final static int WAIT_SEC = 3;
    private final static String UNABLE_TO_GET_THE_LOCK_ERR_MSG = "Unable to get the  lock";

    /**
     * Showcase of transferring money between two accounts.
     */
    public static void main(String[] args) {
        final BankAccount firstAcc = new BankAccount(1000);
        final BankAccount secondAcc = new BankAccount(2000);
        new Thread(() -> {
            try {
                transfer(firstAcc, secondAcc, 500);
            } catch (InsufficientResourcesException | InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
        try {
            transfer(secondAcc, firstAcc, 300);
        } catch (InsufficientResourcesException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will try to transfer money from one account to the other account.
     * If resources(account) is taken by another thread right now, it will wait for {@link #WAIT_SEC}  seconds and then try again.
     * If it still busy it will print an error message.
     *
     * @param firstAcc  account to transfer money from
     * @param secondAcc account to transfer money to
     * @param amount    amount of money
     * @throws InsufficientResourcesException if there is not enough money on the first account
     */
    public static void transfer(BankAccount firstAcc, BankAccount secondAcc, int amount) throws InsufficientResourcesException, InterruptedException {
        if (firstAcc.getBalance() < amount) {
            throw new InsufficientResourcesException();
        }
        //Synchronized access to resources. Possible deadlock while executing transfer(firstAcc, secondAcc..) transfer(secondAcc, firstAcc..)
        if (firstAcc.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
            System.out.println("Got lock for the  first");
            try {
                if (secondAcc.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("Got lock for the second");
                        firstAcc.withdraw(amount);
                        secondAcc.deposit(amount);
                        System.out.println("Transferred from " + firstAcc + " to " + secondAcc + " " + amount);
                    } finally {
                        secondAcc.getLock().unlock();
                    }
                }
            } finally {
                firstAcc.getLock().unlock();
            }
        } else {
            System.out.println(UNABLE_TO_GET_THE_LOCK_ERR_MSG);
            firstAcc.incrementFailCounter();
            secondAcc.incrementFailCounter();
        }
    }
}
