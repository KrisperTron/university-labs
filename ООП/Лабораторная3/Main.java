import Transport.*;

import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws DuplicateModelNameException, ModelPriceOutOfBoundsException {
        try {
            System.out.println("ЗАДАНИЕ 1");
            Transport car1 = new Car("Lada", 2);
            System.out.println("Исходный объект для Задания 1:");
            TransportUtils.printModelsAndPrices(car1);

            File byteFile = new File("transport_byte.bin");
            try (FileOutputStream fos = new FileOutputStream(byteFile)) {
                TransportUtils.outputTransport(car1, fos);
            }

            Transport carFromByte;
            try (FileInputStream fis = new FileInputStream(byteFile)) {
                carFromByte = TransportUtils.inputTransport(fis);
            }
            System.out.println("\n[1] Восстановлено из байтового файла (transport_byte.bin):");
            TransportUtils.printModelsAndPrices(carFromByte);

            File textFile = new File("transport_text.txt");
            try (FileWriter fw = new FileWriter(textFile)) {
                TransportUtils.writeTransport(car1, fw);
            }

            Transport carFromText;
            try (FileReader fr = new FileReader(textFile)) {
                carFromText = TransportUtils.readTransport(fr);
            }
            System.out.println("\n[2] Восстановлено из текстового файла (transport_text.txt):");
            TransportUtils.printModelsAndPrices(carFromText);

            System.out.println("\n[3] Вывод в System.out через writeTransport:");
            OutputStreamWriter osw = new OutputStreamWriter(System.out);
            TransportUtils.writeTransport(car1, osw);

            System.out.println("\n[4] Ввод из System.in через readTransport.");
            System.out.println("Введите построчно: марку, количество моделей, затем имя и цену для каждой:");
            InputStreamReader isr = new InputStreamReader(System.in);
            Transport carFromConsole = TransportUtils.readTransport(isr);

            System.out.println("\nРезультат считывания с консоли:");
            TransportUtils.printModelsAndPrices(carFromConsole);

            Transport carOrig = new Car("Audi", 2);
            Transport motoOrig = new Motorbike("Yamaha", 2);

            File file = new File("transport_ser.dat");

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(carOrig);
                oos.writeObject(motoOrig);
            }

            Transport carRestored;
            Transport motoRestored;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                carRestored = (Transport) ois.readObject();
                motoRestored = (Transport) ois.readObject();
            }

           // carOrig.setBrand("BMW"); // для вывода false

            System.out.println("Car: марка совпадает -> " + carOrig.getBrand().equals(carRestored.getBrand()));
            System.out.println("Car: модели совпадают -> " + Arrays.equals(carOrig.getModelsName(), carRestored.getModelsName()));
            System.out.println("Car: цены совпадают -> " + Arrays.equals(carOrig.getModelPrices(), carRestored.getModelPrices()));

            System.out.println("Moto: марка совпадает -> " + motoOrig.getBrand().equals(motoRestored.getBrand()));
            System.out.println("Moto: модели совпадают -> " + Arrays.equals(motoOrig.getModelsName(), motoRestored.getModelsName()));
            System.out.println("Moto: цены совпадают -> " + Arrays.equals(motoOrig.getModelPrices(), motoRestored.getModelPrices()));

        } catch (DuplicateModelNameException e) {
            System.err.println("Ошибка:" + e.getMessage());
        } catch (ModelPriceOutOfBoundsException e) {
            System.err.println("Ошибка: Недопустимая цена модели! (" + e.getMessage() + ")");
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода при работе с потоками: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
        }
    }
}