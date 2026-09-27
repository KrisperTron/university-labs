package Transport;

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
}