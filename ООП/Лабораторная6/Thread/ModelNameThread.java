package Thread;
import Transport.*;

public class ModelNameThread extends Thread {
    private final Transport transport;

    public ModelNameThread(Transport transport) {
        this.transport = transport;
    }

    @Override
    public void run() {
        String[] models = transport.getModelsName();
        for (String model : models) {
            System.out.println("[ModelNameThread] Модель: " + model);
        }
    }
}