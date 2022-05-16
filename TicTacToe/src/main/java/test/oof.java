package test;

public class oof implements Runnable {
    @Override
    public void run() {
        shared.Instance.num.set(0);
        while (true) {
            shared.Instance.num.set(shared.Instance.num.get() + 1);
            test.print("oof:" + shared.Instance.num.get());
        }
    }
}
