package util;

import java.util.Objects;

public class Pair<T, T2> {
  private T _first;
  private T2 _second;

  public Pair(T first, T2 second) {
    _first = first;
    _second = second;
  }

  public T get_first() {
    return _first;
  }

  public T2 get_second() {
    return _second;
  }

  public void set_first(T value) {
    _first = value;
  }

  public void set_second(T2 value) {
    _second = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(_first, pair._first) && Objects.equals(_second, pair._second);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_first, _second);
  }
}
