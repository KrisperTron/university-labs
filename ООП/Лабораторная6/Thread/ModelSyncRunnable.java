package Thread;

public class ModelSyncRunnable implements Runnable {
    private final TransportSynchronizer synchronizer;

    public ModelSyncRunnable(TransportSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        try {
            while (synchronizer.canPrintModel()) {
                synchronizer.printModel();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}