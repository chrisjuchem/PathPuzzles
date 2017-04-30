package deque;

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  //returns true if the node is a sentinel
  abstract boolean isSentinel();
  //returns the data of the node at the head, if present
  abstract T peek();
}