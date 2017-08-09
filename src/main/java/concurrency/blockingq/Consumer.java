package concurrency.blockingq;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Consumer implements Runnable {
    private static final String GONNA_TAKE_FROM_QUEUE_MSG = "Consumer is going to take a value from queue";
    private static final String RECEIVED_VALUE_FROM_QUEUE_MSG = "Consumer received %s from queue";
    private static final String GONNA_SLEEP_MSG = "Consumer is going to sleep";

    private static final Integer LOWER_BOUND_SLEEP_MILLIS = 1000;
    private static final Integer UPPER_BOUND_SLEEP_MILLIS = 3000;

    SynchronousQueue<Integer> queue;

    public Consumer(SynchronousQueue<Integer> queue) {
        this.queue = queue;
    }

    /**
     * Consumer constantly takes integer from the queue and sleeps for a random time.
     */
    @Override
    public void run() {
        while (true) {
            System.out.println(GONNA_TAKE_FROM_QUEUE_MSG);
            Integer i = null;
            ThreadLocalRandom randomSleep = ThreadLocalRandom.current();

            try {
                i = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(String.format(RECEIVED_VALUE_FROM_QUEUE_MSG, i));

            System.out.println(GONNA_SLEEP_MSG);

            try {
                Thread.sleep(randomSleep.nextInt(LOWER_BOUND_SLEEP_MILLIS, UPPER_BOUND_SLEEP_MILLIS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
