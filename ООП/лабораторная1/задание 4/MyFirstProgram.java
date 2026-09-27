import java.util.Random;

class MyFirstClass {
    public static void main(String[] args) {
        for(String s : args){
            System.out.println(s);
        }

        MySecondClass sec = new MySecondClass(4);
        sec.printArray();

        sec.setE(0, 42);
        sec.printArray();

        System.out.println("Average array: " + sec.ave());
    }
}

class MySecondClass {
    private int[] array;

    public int getE(int index){
        return array[index];
    }

    public void setE(int index, int val){
        array[index] = val;
    }

    public MySecondClass(int size){

        array = new int[size];
        Random rnd = new Random();
        for(int i = 0; i < size; i++){
            array[i] = rnd.nextInt(100);
        }
    }

    public double ave(){
        if(array.length == 0) return 0.0;

        double ave;
        long sum = 0;
        for(int i : array){
            sum += i;
        }
        ave = (double) sum / array.length;
        return ave;
    }

    public void printArray(){
        for(int val : array){
            System.out.print(val + " ");
        }
        System.out.println();
    }
}