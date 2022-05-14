package util;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class Optional<T> {
    private final boolean _isEmpty;
    private T _value;

    private Optional() {
        _isEmpty = true;
    }
    private Optional(T value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        _isEmpty = false;
        _value = value;
    }

    public boolean isPresent() {
        return _value != null;
    }

    public T get() {
        if (_value == null) {
            throw new NoSuchElementException("No value present");
        }
        return _value;
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        if (mapper == null) {
            throw new NullPointerException("Mapper function cannot be null");
        }
        if(_isEmpty) return (Optional<U>) _empty;
        return ofNullable(mapper.apply(_value));
    }

    private static final Optional<?> _empty = new Optional<>();
    public static <T> Optional<T> empty() {
        return (Optional<T>)_empty;
    }
    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }
}
