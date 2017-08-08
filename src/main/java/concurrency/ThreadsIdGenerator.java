package concurrency;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadsIdGenerator {
    public static Integer generateId() {
        return ThreadLocalRandom.current().nextInt();
    }
}
