package concurrency.blockingq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

public class Demo {
    /**
     * This is a showcase of using {@link SynchronousQueue} for synchronizing data exchange between two Threads.
     */
    public static void main(String[] args) {
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();

        Producer producer = new Producer(synchronousQueue);
        Consumer consumer = new Consumer(synchronousQueue);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(producer);
        executorService.execute(consumer);

    }
}
