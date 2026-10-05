import Transport.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Transport[] transports = new Transport[8];
        transports[0] = new Car("Lada", 6);
        transports[1] = new Motorbike("Ural", 5);

        // 1. Байтовая запись в файлы (пишет сырые байты, кодировка не нужна)
        try (OutputStream out = new FileOutputStream("car.transport")) {
            TransportUtils.outputTransport(transports[0], out);
            System.out.println("Автомобиль успешно записан в байтовый файл.");
        } catch (IOException e) {
            System.err.println("Ошибка записи байтового файла: " + e.getMessage());
        }

        try (OutputStream out = new FileOutputStream("bike.transport")) {
            TransportUtils.outputTransport(transports[1], out);
            System.out.println("Мотоцикл успешно записан в байтовый файл.");
        } catch (IOException e) {
            System.err.println("Ошибка записи байтового файла: " + e.getMessage());
        }

        // 2. Байтовое чтение из файлов
        try (InputStream in = new FileInputStream("car.transport")) {
            transports[2] = TransportUtils.inputTransport(in);
            transports[2].setBrand("Прочитанная машина");
        } catch (IOException | DuplicateModelNameException e) {
            System.err.println("Ошибка чтения байтового файла: " + e.getMessage());
        }

        try (InputStream in = new FileInputStream("bike.transport")) {
            transports[3] = TransportUtils.inputTransport(in);
            transports[3].setBrand("Мотоцикл файл");
        } catch (IOException | DuplicateModelNameException e) {
            System.err.println("Ошибка чтения байтового файла: " + e.getMessage());
        }

        // 3. Символьная запись в файлы с явным указанием UTF-8
        try (Writer out = new OutputStreamWriter(new FileOutputStream("car.txt"), StandardCharsets.UTF_8)) {
            TransportUtils.writeTransport(transports[0], out);
            System.out.println("Автомобиль успешно записан через символьный поток (UTF-8).");
        } catch (IOException e) {
            System.err.println("Ошибка записи символьного файла: " + e.getMessage());
        }

        try (Writer out = new OutputStreamWriter(new FileOutputStream("bike.txt"), StandardCharsets.UTF_8)) {
            TransportUtils.writeTransport(transports[1], out);
            System.out.println("Мотоцикл успешно записан через символьный поток (UTF-8).");
        } catch (IOException e) {
            System.err.println("Ошибка записи символьного файла: " + e.getMessage());
        }

        // 4. Символьное чтение из файлов с явным указанием UTF-8
        try (Reader in = new InputStreamReader(new FileInputStream("car.txt"), StandardCharsets.UTF_8)) {
            transports[4] = TransportUtils.readTransport(in);
            transports[4].setBrand("Машина из символов");
        } catch (IOException | DuplicateModelNameException e) {
            System.err.println("Ошибка чтения символьного файла: " + e.getMessage());
        }

        try (Reader in = new InputStreamReader(new FileInputStream("bike.txt"), StandardCharsets.UTF_8)) {
            transports[5] = TransportUtils.readTransport(in);
            transports[5].setBrand("Мотоцикл из символов");
        } catch (IOException | DuplicateModelNameException e) {
            System.err.println("Ошибка чтения символьного файла: " + e.getMessage());
        }

        // 5. Стандартная сериализация Java
        try (OutputStream fileOut = new FileOutputStream("car.serialized");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(transports[0]);
            System.out.println("Автомобиль успешно сериализован.");
        } catch (IOException e) {
            System.err.println("Ошибка сериализации автомобиля: " + e.getMessage());
        }

        try (OutputStream fileOut = new FileOutputStream("bike.serialized");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(transports[1]);
            System.out.println("Мотоцикл успешно сериализован.");
        } catch (IOException e) {
            System.err.println("Ошибка сериализации мотоцикла: " + e.getMessage());
        }

        // 6. Стандартная десериализация Java
        try (InputStream fileIn = new FileInputStream("car.serialized");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            transports[6] = (Transport) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка десериализации автомобиля: " + e.getMessage());
        }

        try (InputStream fileIn = new FileInputStream("bike.serialized");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            transports[7] = (Transport) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка десериализации мотоцикла: " + e.getMessage());
        }

        // 7. Вывод информации о всех полученных объектах
        System.out.println("\nСписок всех объектов:");
        for (Transport transport : transports) {
            if (transport != null) {
                System.out.println("\nМарка: " + transport.getBrand());
                System.out.println("Класс: " + transport.getClass().getSimpleName());
                TransportUtils.printModelsAndPrices(transport);
                System.out.println("Средняя цена: " + TransportUtils.avgPriceModel(transport));
            }
        }

        // 8. Консольный ввод/вывод с явным указанием UTF-8
        System.out.println("\nВведите построчно:");
        System.out.println("1. Код типа (1 - Car, 2 - Motorbike)");
        System.out.println("2. Марка");
        System.out.println("3. Количество моделей");
        System.out.println("4. Название модели 1");
        System.out.println("5. Цена модели 1");
        System.out.println("...");

        try {
            Transport transportFromReader = TransportUtils.readTransport(new InputStreamReader(System.in));
            System.out.println("\nВывод считанного объекта через writeTransport в System.out:");
            OutputStreamWriter osw = new OutputStreamWriter(System.out);
            TransportUtils.writeTransport(transportFromReader, osw);

            System.out.println("\nФорматированный вывод считанного объекта:");
            TransportUtils.printModelsAndPrices(transportFromReader);
            System.out.println("Средняя цена: " + TransportUtils.avgPriceModel(transportFromReader));
        } catch (DuplicateModelNameException e) {
            System.err.println("Ошибка: Модель с таким именем уже существует! (" + e.getMessage() + ")");
        } catch (NumberFormatException e) {
            System.err.println("Ошибка формата числа: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        }
    }
}