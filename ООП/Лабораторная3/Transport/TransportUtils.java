package Transport;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TransportUtils{
    public TransportUtils() {}

    public static double avgPriceModel(Transport transport){
        if(transport == null || transport.getSize() == 0){
            return 0.0;
        }

        double[] prices = transport.getModelPrices();
        double sum = 0.0;
        for(double price : prices){
            sum += price;
        }
        return sum / prices.length;
    }

    public static void printModels(Transport transport){
        if(transport == null){
            System.out.println("транспорт не найден!");
            return;
        }

        System.out.println("Транспорты бренда " + transport.getBrand() + ":");
        String[] names = transport.getModelsName();
        for(String name : names){
            System.out.println("- " + name);
        }
    }

    public static void printPrices(Transport transport){
        if(transport == null){
            System.out.println("транспорт не найден!");
            return;
        }

        System.out.println("Цены за " + transport.getBrand() + ":");
        double[] prices = transport.getModelPrices();
        for(double price : prices){
            System.out.println("- " + price);
        }
    }

    public static void printModelsAndPrices(Transport transport){
        if(transport == null){
            System.out.println("транспорт не найден!");
            return;
        }

        System.out.println("Модель и цена за модель бренда " + transport.getBrand() + ":");
        String[] names = transport.getModelsName();
        double[] prices = transport.getModelPrices();
        for(int i = 0; i < names.length; i++){
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

        Transport transport;
        switch (type) {
            case "Car":
                transport = new Car(brand, 0);
                break;
            case "Motorbike":
                transport = new Motorbike(brand, 0);
                break;
            default:
                throw new IllegalArgumentException("Unknown transport type: " + type);
        }

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

    public static void writeTransport(Transport v, Writer out) {
        PrintWriter pw = new PrintWriter(out);

        pw.println(v.getClass().getSimpleName());

        pw.println(v.getBrand());

        int size = v.getSize();
        pw.println(size);

        String[] names = v.getModelsName();
        double[] prices = v.getModelPrices();

        for (int i = 0; i < size; i++) {
            pw.println(names[i]);
            pw.println(prices[i]);
        }

        pw.flush();
    }

    public static Transport readTransport(Reader in) throws IOException, DuplicateModelNameException {
        BufferedReader br = new BufferedReader(in);

        String type = br.readLine();
        if (type == null) {
            throw new IOException("Unexpected end of stream: missing transport type");
        }

        String brand = br.readLine();
        int size = Integer.parseInt(br.readLine().trim());

        Transport transport;
        switch (type) {
            case "Car":
                transport = new Car(brand, 0);
                break;
            case "Motorbike":
                transport = new Motorbike(brand, 0);
                break;
            default:
                throw new IllegalArgumentException("Unknown transport type: " + type);
        }

        for (int i = 0; i < size; i++) {
            String name = br.readLine();
            double price = Double.parseDouble(br.readLine().trim());
            transport.addModel(name, price);
        }

        return transport;
    }
}