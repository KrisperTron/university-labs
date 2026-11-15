package Thread;
import Transport.*;
import java.util.concurrent.locks.ReentrantLock;

public class ModelLockRunnable implements Runnable {
    private final Transport transport;
    private final ReentrantLock lock;

    public ModelLockRunnable(Transport transport, ReentrantLock lock) {
        this.transport = transport;
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            String[] models = transport.getModelsName();
            for (String model : models) {
                System.out.println("[Lock] Модель: " + model);
            }
        } finally {
            lock.unlock();
        }
    }
}