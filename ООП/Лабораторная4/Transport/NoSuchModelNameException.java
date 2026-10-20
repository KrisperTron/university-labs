package Transport;

public class NoSuchModelNameException extends Exception{
    private final String name;

    public NoSuchModelNameException(String name){
        super("Название модели " + name + " несуществует");
        this.name = name;
    }

    public String getName(){
        return name;
    }
}