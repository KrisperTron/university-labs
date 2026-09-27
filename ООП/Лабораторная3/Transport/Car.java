package Transport;

import java.util.Arrays;
import java.util.Random;
import java.io.Serializable;

public class Car implements Transport{
    private class Model implements Serializable{
        private static final long serialVersionUID = 1L;
        private String name;
        private double price;

        public Model() { }

        public Model(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    private String brand;
    private Model[] models;

    public Car(String brand, int size) {
        this.brand = brand;
        this.models = new Model[size];
        Random rnd = new Random();
        for (int i = 0; i < size; i++) {
            models[i] = new Model(brand + " Model_" + i, rnd.nextDouble(1000, 100000));
        }
    }

    @Override
    public String getBrand(){
        return brand;
    }

    @Override
    public void setBrand(String brand){
        this.brand = brand;
    }

    @Override
    public void setModelName(String oldName, String newName) throws DuplicateModelNameException, NoSuchModelNameException{
        if (oldName.equals(newName)) {
            return;
        }

        for (Model model : models) {
            if (model.name.equals(newName)) {
                throw new DuplicateModelNameException(newName);
            }
        }

        for (Model model : models) {
            if (model.name.equals(oldName)) {
                model.name = newName;
                return;
            }
        }
        throw new NoSuchModelNameException(oldName);
    }

    @Override
    public String[] getModelsName(){
        String[] names = new String[models.length];
        for(int i = 0; i < models.length; i++){
            names[i] = models[i].name;
        }
        return names;
    }

    @Override
    public double getPriceName(String name) throws NoSuchModelNameException{
        for(Model model : models){
            if(model.name.equals(name)){
                return model.price;
            }
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public void setPriceName(String name, double price) throws NoSuchModelNameException{
        if(price < 0){
            throw new ModelPriceOutOfBoundsException(price);
        }
        for(Model model : models){
            if(model.name.equals(name)){
                model.price = price;
                return;
            }
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public double[] getModelPrices(){
        double[] prices = new double[models.length];
        for(int i = 0; i < models.length; i++){
            prices[i] = models[i].price;
        }
        return prices;
    }

    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException{
        if(price < 0){
            throw new ModelPriceOutOfBoundsException(price);
        }

        for(Model model : models){
            if(model.name.equals(name)){
                throw new DuplicateModelNameException(name);
            }
        }

        models = Arrays.copyOf(models, models.length + 1);
        models[models.length - 1] = new Model(name, price);
    }

    @Override
    public void deleteModel(String name) throws NoSuchModelNameException{
        int index = -1;
        for(int i = 0; i < models.length; i++){
            if(models[i].name.equals(name)){
                index = i;
                break;
            }
        }

        if(index == -1){
            throw new NoSuchModelNameException(name);
        }

        int num = models.length - index - 1;
        if(num > 0){
            System.arraycopy(models, index + 1, models, index, num);
        }
        models = Arrays.copyOf(models, models.length - 1);
    }

    @Override
    public int getSize(){
        return models.length;
    }
}