package Thread;
import Transport.*;

import java.util.concurrent.locks.ReentrantLock;

public class PriceLockRunnable implements Runnable {
    private final Transport transport;
    private final ReentrantLock lock;

    public PriceLockRunnable(Transport transport, ReentrantLock lock) {
        this.transport = transport;
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            double[] prices = transport.getModelPrices();
            for (double price : prices) {
                System.out.printf("[Lock] Цена: %.2f%n", price);
            }
        } finally {
            lock.unlock();
        }
    }
}