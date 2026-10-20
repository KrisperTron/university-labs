package Transport;

import java.util.Random;
import java.io.Serializable;
import java.util.Objects;
import java.util.Arrays;

public class Motorbike implements Transport{
    private class Model implements Serializable, Cloneable{
        private static final long serialVersionUID = 1L;
        String name = null;
        double price = Double.NaN;
        Model prev = null;
        Model next = null;

        public Model() {}

        public Model(String name, double price){
            this.name = name;
            this.price = price;
        }

        public Model(String name, double price, Model prev, Model next){
            this.name = name;
            this.price = price;
            this.prev = prev;
            this.next = next;
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

    private int size = 0;
    private Model head;
    private transient long lastModified;
    private String brand;

    {
        lastModified = System.currentTimeMillis();
    }

    public Motorbike(String brand, int modelSize){
        this.brand = brand;
        head = new Model();
        head.prev = head;
        head.next = head;
        String fix = "";
        Random rnd = new Random();
        for(int i = 0; i < modelSize; i++){
            try{
                addModel(brand + " Model_" + fix + i, rnd.nextDouble(1000, 100000));
            }
            catch(DuplicateModelNameException e){
                System.out.println("Ошибка: " + e.getMessage());
                fix += "0";
            }
            catch(ModelPriceOutOfBoundsException e){
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        updateModified();
    }

    public void updateModified(){
        this.lastModified = System.currentTimeMillis();
    }

    public long getLastModified() {
        return lastModified;
    }

    @Override
    public String getBrand(){
        return brand;
    }

    @Override
    public void setBrand(String brand){
        this.brand = brand;
        updateModified();
    }

    @Override
    public void setModelName(String oldName, String newName) throws DuplicateModelNameException, NoSuchModelNameException{
        if(oldName.equals(newName)){
            return;
        }

        Model p = head.next;
        while(p != head){
            if(p.name.equals(newName)){
                throw new DuplicateModelNameException(newName);
            }
            p = p.next;
        }

        p = head.next;
        while(p != head){
            if(p.name.equals(oldName)){
                p.name = newName;
                updateModified();
                return;
            }
            p = p.next;
        }
        throw new NoSuchModelNameException(oldName);
    }

    @Override
    public String[] getModelsName(){
        Model p = head.next;
        String[] models = new String[size];
        int index = 0;
        while(p != head){
            models[index++] = p.name;
            p = p.next;
        }
        return models;
    }

    @Override
    public double getPriceName(String name) throws NoSuchModelNameException{
        Model p = head.next;
        while(p != head){
            if(p.name.equals(name)){
                return p.price;
            }
            p = p.next;
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public void setPriceName(String name, double price) throws NoSuchModelNameException{
        if(price < 0){
            throw new ModelPriceOutOfBoundsException(price);
        }

        Model p = head.next;
        while(p != head){
            if(p.name.equals(name)){
                p.price = price;
                updateModified();
                return;
            }
            p = p.next;
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public double[] getModelPrices(){
        Model p = head.next;
        double[] prices = new double[size];
        int index = 0;
        while(p != head){
            prices[index++] = p.price;
            p = p.next;
        }
        return prices;
    }

    @Override
    public void addModel(String name, double price) throws DuplicateModelNameException{
        if(price < 0){
            throw new ModelPriceOutOfBoundsException(price);
        }

        Model p = head.next;
        while(p != head){
            if(p.name.equals(name)){
                throw new DuplicateModelNameException(name);
            }
            p = p.next;
        }

        Model model = new Model(name, price);

        model.prev = head.prev;
        model.next = head;
        head.prev.next = model;
        head.prev = model;

        size++;
        updateModified();
    }

    @Override
    public void deleteModel(String name) throws NoSuchModelNameException{
        Model p = head.next;
        while(p != head){
            if(p.name.equals(name)){
                p.prev.next = p.next;
                p.next.prev = p.prev;

                p.prev = null;
                p.next = null;

                size--;
                updateModified();
                return;
            }
            p = p.next;
        }
        throw new NoSuchModelNameException(name);
    }

    @Override
    public int getSize(){
        return size;
    }

    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer("Мотоцикл марка: ");
        buffer.append(brand);
        buffer.append("\nЧисло моделей: ");
        buffer.append(size);
        buffer.append("\nСписок моделей: ");
        int i = 1;
        Model p = head.next;
        while (p != head){
            buffer.append("\n");
            buffer.append(i);
            buffer.append(") ");
            buffer.append(p);
            i++;
            p = p.next;
        }
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(obj instanceof Transport){
            Transport transport = (Transport) obj;
            if(Objects.equals(brand, transport.getBrand())){
                if(transport.getSize() == size){
                    return Arrays.equals(transport.getModelsName(), getModelsName())
                            && Arrays.equals(transport.getModelPrices(), getModelPrices());
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        int result = Objects.hashCode(brand);
        result = 31 * result + Arrays.hashCode(getModelsName());
        result = 31 * result + Arrays.hashCode(getModelPrices());
        return result;
    }

    @Override
    public Object clone(){
        Motorbike result = null;
        try {
            result = (Motorbike) super.clone();

            result.head = new Model();
            result.head.prev = result.head;
            result.head.next = result.head;
            result.size = 0;

            Model p = this.head.next;
            while (p != this.head){
                result.addModel(p.name, p.price);
                p = p.next;
            }
        } catch (CloneNotSupportedException | DuplicateModelNameException e) {
            e.printStackTrace();
        }
        return result;
    }
}