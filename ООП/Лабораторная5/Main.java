import Transport.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.*;


public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Задание 1 ===");
            if (args.length < 4) {
                System.out.println("Ошибка. Вы не ввели 4 аргумента: полное название класса, метод, аргумент1 метода, аргумент2 метода. Поэтому будут использоваться значения по умолчанию:");
                args = new String[]{"Transport.Car", "setPriceName", "Toyota_1", "77777.7"};
            }

            String className = args[0];
            String methodName = args[1];
            String targetModelName = args[2];
            double newPrice = Double.parseDouble(args[3]);

            System.out.printf("Параметры: класс=%s, метод=%s, модель=%s, новая цена=%.2f%n",
                    className, methodName, targetModelName, newPrice);

            Class<?> c = Class.forName(className);
            Constructor<?> constructor = c.getConstructor(String.class, int.class);
            Transport reflectionCar = (Transport) constructor.newInstance("Toyota", 3);

            System.out.println("\nДо вызова метода:");
            System.out.println(reflectionCar);

            Method method = c.getMethod(methodName, String.class, double.class);
            method.invoke(reflectionCar, targetModelName, newPrice);

            System.out.println("\nПосле вызова метода через рефлексию:");
            System.out.println(reflectionCar);
            TransportUtils.printModelsAndPrices(reflectionCar);

            System.out.println("=== Задание 2===");
            Transport createdCar = TransportUtils.createTransport("Mazda", 2, reflectionCar);
            System.out.println("Созданный объект того же класса: " + createdCar.getClass());

            System.out.println("\n===Задания 3, 4, 5===");

            Transport scooter = new Scooter("Vespa", 2);
            scooter.addModel("Vespa_Sprint", 3200.0);
            scooter.setModelName("Vespa_0", "Vespa_Prim");
            scooter.setPriceName("Vespa_Prim", 4100.0);
            scooter.deleteModel("Vespa_1");
            System.out.println("Скутер: " + scooter);

            Transport quadBike = new Quadbike("Polaris", 2);
            quadBike.addModel("Polaris_Sport", 8500.0);
            quadBike.setModelName("Polaris_0", "Polaris_Justy");
            quadBike.setPriceName("Polaris_Justy", 9200.0);
            quadBike.deleteModel("Polaris_1");
            System.out.println("Квадроцикл: " + quadBike);

            Transport moped = new Moped("Alpha", 2);
            moped.addModel("Alpha_RX", 1500.0);
            moped.setModelName("Alpha_0", "Alpha_7UP");
            moped.setPriceName("Alpha_7UP", 1800.0);
            moped.deleteModel("Alpha_1");
            System.out.println("Мопед: " + moped);

            System.out.println("\n=== Задание 6===");
            double avgAll = TransportUtils.getAverageTransportsPrice(scooter, quadBike, moped);
            System.out.printf("Средняя цена всех моделей (Scooter, Quadbike, Moped): %.2f%n", avgAll);

            System.out.println("\n===Задание 7===");

            File file = new File("transport_text.txt");
            try (FileWriter fileWriter = new FileWriter(file))
            {
                TransportUtils.writeTransport(quadBike, fileWriter);
                System.out.println("Данные успешно записаны в файл: " + file.getAbsolutePath());
            }

            Transport restoredQuadBike;
            try (FileReader fileReader = new FileReader(file))
            {
                restoredQuadBike = TransportUtils.readTransport(fileReader);
                System.out.println("Объект успешно прочитан из файла.");
                System.out.println(restoredQuadBike);
                System.out.println("Объекты равны (equals): " + quadBike.equals(restoredQuadBike));
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: Класс не найден -> " + e.getMessage());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            System.err.println("Ошибка рефлексии: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Ошибка внутри вызванного метода: " + e.getCause().getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка при выполнении: " + e.getMessage());
        }
    }
}