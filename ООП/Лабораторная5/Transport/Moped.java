package Transport;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class Moped implements Transport {

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
    private LinkedList<Model> models;

    public Moped(String brand, int size) {
        this.brand = brand;
        this.models = new LinkedList<>();
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
        int index = 0;
        for (Model m : models) {
            names[index++] = m.name;
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
        int index = 0;
        for (Model m : models) {
            prices[index++] = m.price;
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
        Iterator<Model> iterator = models.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().name.equals(name)) {
                iterator.remove();
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
        StringBuffer buffer = new StringBuffer("Мопед марка: ");
        buffer.append(brand);
        buffer.append("\nЧисло моделей: ");
        buffer.append(models.size());
        buffer.append("\nСписок моделей: ");
        int i = 1;
        for (Model m : models) {
            buffer.append("\n");
            buffer.append(i++);
            buffer.append(") ");
            buffer.append(m);
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
        Moped result = null;
        try {
            result = (Moped) super.clone();
            result.models = new LinkedList<>();
            for (Model m : this.models) {
                result.models.add((Model) m.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }
}