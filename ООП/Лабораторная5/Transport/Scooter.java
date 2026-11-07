package Transport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Scooter implements Transport {
    private String brand;
    private HashMap<String, Double> models;

    public Scooter(String brand, int size) {
        this.brand = brand;
        this.models = new HashMap<>();
        Random rnd = new Random();
        for (int i = 0; i < size; i++) {
            models.put(brand + "_" + i, rnd.nextDouble(1000, 100000));
        }
    }

    @Override
    public String getBrand() {
        return brand;
    }

    @Override
    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public void setModelName(String oldName, String newName) throws DuplicateModelNameException, NoSuchModelNameException {
        if (!models.containsKey(oldName)) {
            throw new NoSuchModelNameException(oldName);
        }
        if (oldName.equals(newName)) {
            return;
        }
        if (models.containsKey(newName)) {
            throw new DuplicateModelNameException(newName);
        }
        Double price = models.remove(oldName);
        models.put(newName, price);
    }

    @Override
    public String[] getModelsName() {
        return models.keySet().toArray(new String[0]);
    }

    @Override
    public double getPriceName(String name) throws NoSuchModelNameException {
        if (!models.containsKey(name)) {
            throw new NoSuchModelNameException(name);
        }
        return models.get(name);
    }

    @Override
    public void setPriceName(String name, double price) throws NoSuchModelNameException {
        if (price < 0) {
            throw new ModelPriceOutOfBoundsException(price);
        }
        if (!models.containsKey(name)) {
            throw new NoSuchModelNameException(name);
        }
        models.put(name, price);
    }

    @Override
    public double[] getModelPrices() {
        double[] prices = new double[models.size()];
        int i = 0;
        for (Double price : models.values()) {
            prices[i++] = price;
        }
        return prices;
    }

    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException {
        if (price < 0) {
            throw new ModelPriceOutOfBoundsException(price);
        }
        if (models.containsKey(name)) {
            throw new DuplicateModelNameException(name);
        }
        models.put(name, price);
    }

    @Override
    public void deleteModel(String name) throws NoSuchModelNameException {
        if (!models.containsKey(name)) {
            throw new NoSuchModelNameException(name);
        }
        models.remove(name);
    }

    @Override
    public int getSize() {
        return models.size();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("Скутер марка: ");
        buffer.append(brand);
        buffer.append("\nЧисло моделей: ");
        buffer.append(models.size());
        buffer.append("\nСписок моделей: ");
        int i = 1;
        for (Map.Entry<String, Double> entry : models.entrySet()) {
            buffer.append("\n");
            buffer.append(i++);
            buffer.append(") ");
            buffer.append(entry.getKey()).append(" : ").append(entry.getValue());
        }
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Transport) {
            Transport transport = (Transport) obj;
            if (Objects.equals(brand, transport.getBrand())) {
                if (transport.getSize() == models.size()) {
                    return Arrays.equals(transport.getModelsName(), getModelsName())
                            && Arrays.equals(transport.getModelPrices(), getModelPrices());
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(brand);
        result = 31 * result + Arrays.hashCode(getModelsName());
        result = 31 * result + Arrays.hashCode(getModelPrices());
        return result;
    }

    @Override
    public Object clone() {
        Scooter result = null;
        try {
            result = (Scooter) super.clone();
            result.models = new HashMap<>(this.models);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }
}