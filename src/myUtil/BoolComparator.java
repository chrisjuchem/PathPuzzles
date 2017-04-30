package myUtil;

//a binary comparator between 2 objects of the same type
public interface BoolComparator<T> {
  boolean compare(T one, T two);
}
