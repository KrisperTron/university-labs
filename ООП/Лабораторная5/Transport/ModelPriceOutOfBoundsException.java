package Transport;

public class ModelPriceOutOfBoundsException extends RuntimeException{
    private final double price;

    public ModelPriceOutOfBoundsException(double price){
        super("Цена " + price + " неверная");
        this.price = price;
    }

    public double getPrice(){
        return price;
    }
}