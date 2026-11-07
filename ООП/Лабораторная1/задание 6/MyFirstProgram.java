import MyFirstPackage.MyFirstPackage;

class MyFirstClass {
    public static void main(String[] args) {
        for(String s : args){
            System.out.println(s);
        }

        MyFirstPackage sec = new MyFirstPackage(4);
        sec.printArray();

        sec.setE(0, 42);
        sec.printArray();

        System.out.println("Average array: " + sec.ave());
    }
}