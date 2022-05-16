package test;

public class test {
    public static synchronized void print(String output) {
        System.out.println(output);
    }


    public static void main(String[] args) {
        new Thread(new ugh()).start();
        new Thread(new oof()).start();
        while (true) {
        }
    }
}
