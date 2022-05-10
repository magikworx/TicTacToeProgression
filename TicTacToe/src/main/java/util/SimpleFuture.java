package util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class SimpleFuture<V> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private V value;

    public boolean isDone() {
        return latch.getCount() == 0;
    }

    public V get() throws InterruptedException {
        latch.await();
        return value;
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return value;
        } else {
            throw new TimeoutException();
        }
    }

    public void put(V result) {
        value = result;
        latch.countDown();
    }
}
