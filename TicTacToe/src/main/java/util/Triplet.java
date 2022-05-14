package util;

import java.util.Objects;

public class Triplet<T, T2, T3> {
    private T _first;
    private T2 _second;
    private T3 _third;

    public Triplet(T first, T2 second, T3 third) {
        _first = first;
        _second = second;
        _third = third;
    }

    public T get_first() {
        return _first;
    }

    public T2 get_second() {
        return _second;
    }

    public T3 get_third() {
        return _third;
    }

    public void set_first(T value) {
        _first = value;
    }

    public void set_second(T2 value) {
        _second = value;
    }

    public void set_third(T3 value) {
        _third = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(_first, triplet._first)
                && Objects.equals(_second, triplet._second)
                && Objects.equals(_third, triplet._third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_first, _second, _third);
    }
}
