package Thread;
import Transport.*;

public class MarkRunnable implements Runnable {
    private final Transport transport;

    public MarkRunnable(Transport transport) {
        this.transport = transport;
    }

    @Override
    public void run() {
        System.out.println("Марка ТС: " + transport.getBrand() + " (выполнено потоком " + Thread.currentThread().getName() + ")");
    }
}