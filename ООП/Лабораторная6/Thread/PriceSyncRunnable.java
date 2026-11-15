package Thread;

public class PriceSyncRunnable implements Runnable {
    private final TransportSynchronizer synchronizer;

    public PriceSyncRunnable(TransportSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        try {
            while (synchronizer.canPrintPrice()) {
                synchronizer.printPrice();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}