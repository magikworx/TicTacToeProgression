package test.threadlike;

public class shared {
    public static final shared Instance = new shared();

    private shared() {
        num.set(0);
    }

    public ThreadLocal<Integer> num = new ThreadLocal<>();
}
