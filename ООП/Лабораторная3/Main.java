import Transport.*;

public class Main {
    public static void main(String[] args) {
        Transport car = new Car("Toyota", 3);
        Transport moto = new Motorbike("Honda", 3);

        System.out.println("Все машины: ");
        TransportUtils.printModelsAndPrices(car);
        System.out.println("Средняя цена моделей: " + TransportUtils.avgPriceModel(car));
        System.out.println("\nВсе мотоциклы: ");
        TransportUtils.printModelsAndPrices(moto);
        System.out.println("Средняя цена моделей: " + TransportUtils.avgPriceModel(moto));

        System.out.println("\nМодификация Car: ");
        try {
            String[] carModels = car.getModelsName();
            car.setModelName(carModels[0], "Camry");
            car.setPriceName("Camry", 25000.0);
            car.deleteModel(carModels[1]);
            System.out.println("После изменений:");
            TransportUtils.printModelsAndPrices(car);
        }
        catch (DuplicateModelNameException | NoSuchModelNameException e) {
            System.err.println("Ошибка при изменении Car: " + e.getMessage());
        }

            System.out.println("\nМодификация Motorbike: ");
        try {
            String[] motoModels = moto.getModelsName();
            moto.setModelName(motoModels[0], "MT-09");
            moto.deleteModel(motoModels[1]);
            System.out.println("После изменений:");
            TransportUtils.printModelsAndPrices(moto);
        }
        catch (DuplicateModelNameException | NoSuchModelNameException e) {
            System.err.println("Ошибка при изменении Motorbike: " + e.getMessage());
        }
    }
}