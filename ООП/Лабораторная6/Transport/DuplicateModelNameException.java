package Transport;

public class DuplicateModelNameException extends Exception{
    private final String name;

    public DuplicateModelNameException(String name){
        super("Название модели " + name + " уже существует");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}