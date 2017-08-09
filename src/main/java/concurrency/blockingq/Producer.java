package concurrency.blockingq;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Producer implements Runnable {
    private static final String GONNA_SLEEP_MSG = "Producer is going to sleep";
    private static final String GONNA_PUT_IN_QUEUE_MSG = "Producer is going to put %s in the queue";

    public static final Integer CNT = 5;
    private static final Integer LOWER_BOUND_SLEEP_MILLIS = 2000;
    private static final Integer UPPER_BOUND_SLEEP_MILLIS = 5000;

    private SynchronousQueue<Integer> queue;

    public Producer(SynchronousQueue<Integer> queue) {
        this.queue = queue;
    }

    /**
     * Producer puts integers to the synchronized queue and sleeps for random time {@link #CNT} times.
     */
    @Override
    public void run() {
        ThreadLocalRandom randomSleep = ThreadLocalRandom.current();

        for (int i = 0; i < CNT; i++) {

            try {
                System.out.println(GONNA_SLEEP_MSG);
                Thread.sleep(randomSleep.nextInt(LOWER_BOUND_SLEEP_MILLIS, UPPER_BOUND_SLEEP_MILLIS));

                System.out.println(String.format(GONNA_PUT_IN_QUEUE_MSG, i));
                queue.put(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
