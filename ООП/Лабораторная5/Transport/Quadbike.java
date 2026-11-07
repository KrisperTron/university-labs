package Transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Quadbike implements Transport {

    private class Model implements Serializable, Cloneable {
        private static final long serialVersionUID = 1L;
        private String name;
        private double price;

        public Model() {}

        public Model(String name, double price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return name + " : " + price;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    private String brand;
    private ArrayList<Model> models;

    public Quadbike(String brand, int size) {
        this.brand = brand;
        this.models = new ArrayList<>(size);
        Random rnd = new Random();

        for (int i = 0; i < size; i++) {
            this.models.add(new Model(brand + "_" + i, rnd.nextDouble(1000, 100000)));
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
        if (oldName.equals(newName)) {
            return;
        }

        for (Model m : models) {
            if (m.name.equals(newName)) {
                throw new DuplicateModelNameException(newName);
            }
        }

        for (Model m : models) {
            if (m.name.equals(oldName)) {
                m.name = newName;
                return;
            }
        }
        throw new NoSuchModelNameException(oldName);
    }

    @Override
    public String[] getModelsName() {
        String[] names = new String[models.size()];
        for (int i = 0; i < models.size(); i++) {
            names[i] = models.get(i).name;
        }
        return names;
    }

    @Override
    public double getPriceName(String name) throws NoSuchModelNameException {
        for (Model m : models) {
            if (m.name.equals(name)) {
                return m.price;
            }
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public void setPriceName(String name, double price) throws NoSuchModelNameException {
        if (price < 0) {
            throw new ModelPriceOutOfBoundsException(price);
        }
        for (Model m : models) {
            if (m.name.equals(name)) {
                m.price = price;
                return;
            }
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public double[] getModelPrices() {
        double[] prices = new double[models.size()];
        for (int i = 0; i < models.size(); i++) {
            prices[i] = models.get(i).price;
        }
        return prices;
    }

    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException {
        if (price < 0) {
            throw new ModelPriceOutOfBoundsException(price);
        }
        for (Model m : models) {
            if (m.name.equals(name)) {
                throw new DuplicateModelNameException(name);
            }
        }
        models.add(new Model(name, price));
    }

    @Override
    public void deleteModel(String name) throws NoSuchModelNameException {
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).name.equals(name)) {
                models.remove(i);
                return;
            }
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public int getSize() {
        return models.size();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("Квадроцикл марка: ");
        buffer.append(brand);
        buffer.append("\nЧисло моделей: ");
        buffer.append(models.size());
        buffer.append("\nСписок моделей: ");
        for (int i = 0; i < models.size(); i++) {
            buffer.append("\n");
            buffer.append(i + 1);
            buffer.append(") ");
            buffer.append(models.get(i));
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
        Quadbike result = null;
        try {
            result = (Quadbike) super.clone();
            result.models = new ArrayList<>(this.models.size());
            for (Model m : this.models) {
                result.models.add((Model) m.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }
}