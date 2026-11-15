package Thread;
import Transport.*;

public class PriceThread extends Thread {
    private final Transport transport;

    public PriceThread(Transport transport) {
        this.transport = transport;
    }

    @Override
    public void run() {
        double[] prices = transport.getModelPrices();
        for (double price : prices) {
            System.out.printf("[PriceThread] Цена: %.2f%n", price);
        }
    }
}