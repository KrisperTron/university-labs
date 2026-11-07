package Transport;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class TransportUtils {
    public TransportUtils() {}

    public static double avgPriceModel(Transport transport) {
        if (transport == null || transport.getSize() == 0) {
            return 0.0;
        }

        double[] prices = transport.getModelPrices();
        double sum = 0.0;
        for (double price : prices) {
            sum += price;
        }
        return sum / prices.length;
    }

    public static void printModels(Transport transport) {
        if (transport == null) {
            System.out.println("транспорт не найден!");
            return;
        }

        System.out.println("Транспорты бренда " + transport.getBrand() + ":");
        String[] names = transport.getModelsName();
        for (String name : names) {
            System.out.println("- " + name);
        }
    }

    public static void printPrices(Transport transport) {
        if (transport == null) {
            System.out.println("транспорт не найден!");
            return;
        }

        System.out.println("Цены за " + transport.getBrand() + ":");
        double[] prices = transport.getModelPrices();
        for (double price : prices) {
            System.out.println("- " + price);
        }
    }

    public static void printModelsAndPrices(Transport transport) {
        if (transport == null) {
            System.out.println("транспорт не найден!");
            return;
        }

        System.out.println("Модель и цена за модель бренда " + transport.getBrand() + ":");
        String[] names = transport.getModelsName();
        double[] prices = transport.getModelPrices();
        for (int i = 0; i < names.length; i++) {
            System.out.println("Модель: " + names[i] + ", цена: " + prices[i] + ".");
        }
    }

    public static void outputTransport(Transport v, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        byte[] typeBytes = v.getClass().getSimpleName().getBytes(StandardCharsets.UTF_8);
        dos.writeInt(typeBytes.length);
        dos.write(typeBytes);

        byte[] brandBytes = v.getBrand().getBytes(StandardCharsets.UTF_8);
        dos.writeInt(brandBytes.length);
        dos.write(brandBytes);

        int size = v.getSize();
        dos.writeInt(size);

        String[] names = v.getModelsName();
        double[] prices = v.getModelPrices();

        for (int i = 0; i < size; i++) {
            byte[] modelName = names[i].getBytes(StandardCharsets.UTF_8);

            dos.writeInt(modelName.length);
            dos.write(modelName);
            dos.writeDouble(prices[i]);
        }
        dos.flush();
    }

    public static Transport inputTransport(InputStream in) throws IOException, DuplicateModelNameException {
        DataInputStream dis = new DataInputStream(in);

        int typeLen = dis.readInt();
        byte[] typeBytes = new byte[typeLen];
        dis.readFully(typeBytes);
        String type = new String(typeBytes, StandardCharsets.UTF_8);

        int readLen = dis.readInt();
        byte[] brandBytes = new byte[readLen];
        dis.readFully(brandBytes);
        String brand = new String(brandBytes, StandardCharsets.UTF_8);

        Transport transport = instantiateByClassName(type, brand);

        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            int nameLen = dis.readInt();

            byte[] nameBytes = new byte[nameLen];
            dis.readFully(nameBytes);

            String name = new String(nameBytes, StandardCharsets.UTF_8);
            double price = dis.readDouble();

            transport.addModel(name, price);
        }

        return transport;
    }

    // Задание 2
    public static Transport createTransport(String brand, int size, Transport v) {
        if (v == null) {
            return null;
        }
        try {
            Class<?> c = v.getClass();
            Constructor<?> constructor = c.getConstructor(String.class, int.class);
            return (Transport) constructor.newInstance(brand, size);
        } catch (Exception e) {
            return null;
        }
    }

    // Задание 6
    public static double getAverageTransportsPrice(Transport... transports) {
        if (transports == null || transports.length == 0) {
            return 0.0;
        }
        double totalSum = 0.0;
        int totalModels = 0;

        for (Transport transport : transports) {
            if (transport != null) {
                double[] prices = transport.getModelPrices();
                for (double price : prices) {
                    totalSum += price;
                    totalModels++;
                }
            }
        }
        return totalModels == 0 ? 0.0 : totalSum / totalModels;
    }

    private static Transport instantiateByClassName(String className, String brand) {
        if (className.contains(".")) {
            className = className.substring(className.lastIndexOf('.') + 1);
        }

        switch (className) {
            case "Car":
                return new Car(brand, 0);
            case "Motorbike":
                return new Motorbike(brand, 0);
            case "Scooter":
                return new Scooter(brand, 0);
            case "Quadbike":
                return new Quadbike(brand, 0);
            case "Moped":
                return new Moped(brand, 0);
            default:
                throw new IllegalArgumentException("Неизвестный тип транспорта: " + className);
        }
    }

    // Задание 7
    public static void writeTransport(Transport v, Writer out) {
        PrintWriter pw = new PrintWriter(out);
        pw.printf("%s%n", v.getClass().getSimpleName());
        pw.printf("%s%n", v.getBrand());

        int count = v.getSize();
        pw.printf("%d%n", count);

        String[] names = v.getModelsName();
        double[] prices = v.getModelPrices();
        for (int i = 0; i < count; i++) {
            pw.printf("%s%n", names[i]);
            pw.printf(Locale.US, "%f%n", prices[i]);
        }
        pw.flush();
    }

    // Задание 7
    public static Transport readTransport(Reader in) throws DuplicateModelNameException {
        Scanner scanner = new Scanner(in);
        scanner.useLocale(Locale.US);

        String className = scanner.next().trim();
        String brand = scanner.next().trim();
        int count = Integer.parseInt(scanner.next().trim());

        Transport transport = instantiateByClassName(className, brand);

        for (int i = 0; i < count; i++) {
            String name = scanner.next().trim();
            double price = Double.parseDouble(scanner.next().trim());
            transport.addModel(name, price);
        }
        return transport;
    }
}