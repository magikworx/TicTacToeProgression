package test;

public class ugh implements Runnable {
    @Override
    public void run() {
        shared.Instance.num.set(0);
        while (true) {
            test.print("ugh: " + shared.Instance.num.get());
        }
    }
}
