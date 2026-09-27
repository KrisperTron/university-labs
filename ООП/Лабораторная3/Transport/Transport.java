package Transport;

import java.io.Serializable;

public interface Transport extends Serializable{
    public abstract String getBrand();
    public abstract void setBrand(String brand);

    public abstract String[] getModelsName();
    public abstract double[] getModelPrices();

    public abstract double getPriceName(String name) throws NoSuchModelNameException;
    public abstract void setPriceName(String name, double price) throws NoSuchModelNameException;

    public abstract void setModelName(String oldName, String newName) throws DuplicateModelNameException, NoSuchModelNameException;
    public abstract void addModel(String name, double price) throws DuplicateModelNameException;
    public abstract void deleteModel(String name) throws NoSuchModelNameException;

    public abstract int getSize();
}