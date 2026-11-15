import Transport.*;
import Thread.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // 1 задание
        System.out.println("=== ЗАДАНИЕ 1: Обычный запуск ===");
        Transport car1 = new Car("Toyota", 5);
        PriceThread priceThread1 = new PriceThread(car1);
        ModelNameThread modelThread1 = new ModelNameThread(car1);

        priceThread1.start();
        modelThread1.start();
        priceThread1.join();
        modelThread1.join();

        System.out.println("\n=== Варьирование приоритетов ===");
        PriceThread priceThread2 = new PriceThread(car1);
        ModelNameThread modelThread2 = new ModelNameThread(car1);
        priceThread2.setPriority(Thread.MIN_PRIORITY);
        modelThread2.setPriority(Thread.MAX_PRIORITY);

        priceThread2.start();
        modelThread2.start();
        priceThread2.join();
        modelThread2.join();

/*
        // 2 задание
        System.out.println("\n=== ЗАДАНИЕ 2: Синхронизация (модель - цена - модель - цена) ===");
        Transport motorbike = new Motorbike("Yamaha", 5);
        TransportSynchronizer synchronizer = new TransportSynchronizer(motorbike);

        // Проверим методы canPrintModel() и canPrintPrice() до старта
        System.out.println("До старта: canPrintModel=" + synchronizer.canPrintModel() + ", canPrintPrice=" + synchronizer.canPrintPrice());

        Thread syncModelThread = new Thread(new ModelSyncRunnable(synchronizer));
        Thread syncPriceThread = new Thread(new PriceSyncRunnable(synchronizer));
        // Даже при разных приоритетах порядок "модель - цена" строго сохраняется
        syncPriceThread.setPriority(Thread.MAX_PRIORITY);
        syncModelThread.setPriority(Thread.MIN_PRIORITY);

        syncModelThread.start();
        syncPriceThread.start();
        syncModelThread.join();
        syncPriceThread.join();

        System.out.println("После завершения: canPrintModel=" + synchronizer.canPrintModel()
                + ", canPrintPrice=" + synchronizer.canPrintPrice());
*/

/*
        // 3 задание: сначала все модели, затем все цены или наоборот
        System.out.println("\n=== ЗАДАНИЕ 3: ReentrantLock ===");
        Transport quadBike = new Quadbike("Polaris", 5);
        ReentrantLock lock = new ReentrantLock();

        Thread lockModelThread = new Thread(new ModelLockRunnable(quadBike, lock));
        Thread lockPriceThread = new Thread(new PriceLockRunnable(quadBike, lock));

        lockPriceThread.start();
        lockModelThread.start();

        lockPriceThread.join();
        lockModelThread.join();
*/

/*
        // 4 Задание
        System.out.println("\n=== ЗАДАНИЕ 4: Пул из 2 потоков для 4 транспортных средств ===");
        Transport[] transports = {
                new Car("Audi", 2),
                new Motorbike("Kawasaki", 2),
                new Scooter("Vespa", 2),
                new Moped("Honda", 2)
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (Transport v : transports) {
            executor.execute(new MarkRunnable(v));
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
*/

/*
        // 5 задание
        System.out.println("\n=== ЗАДАНИЕ 5===");
        String[] fileNames = {"Cadillac.txt", "Changan.txt", "Subaru.txt", "Tesla.txt", "Toyota.txt"};

        // потоки ждут освобождения места
        System.out.println("\n--- Метод put(), размер очереди = 1 ---");
        runQueueTest(fileNames, 1, false);

        // очередь будет переполнена, программа навсегда зависнет
        System.out.println("\n--- Метод add(), размер очереди = 1 ---");
        runQueueTest(fileNames, 1, true);
    }

    private static void runQueueTest(String[] fileNames, int capacity, boolean useAdd) throws InterruptedException {
        BlockingQueue<Transport> queue = new ArrayBlockingQueue<>(capacity);
        for (String fileName : fileNames) {
            Thread t = new Thread(new FileTransportRunnable(fileName, queue, useAdd));
            t.start();
        }

        for (int i = 0; i < fileNames.length; i++) {
            Transport v = queue.take();
            System.out.println("Получено ТС из очереди: марка = " + v.getBrand() + ", количество моделей = " + v.getSize());
        }
       */

    }
}