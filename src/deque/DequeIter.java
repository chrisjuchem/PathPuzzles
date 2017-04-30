package deque;


import java.util.Iterator;

// Represents an iterator over Deques
class DequeIter<T> implements Iterator<T> {
  private ANode<T> next;
  DequeIter(Deque<T> d) {
    this.next = d.header.next;
  }
  public boolean hasNext() {
    return !this.next.isSentinel();
  }
  public T next() {
    if (this.hasNext()) {
      T ret = ((Node<T>) next).data;
      this.next = this.next.next;
      return ret;
    }
    else {
      throw new RuntimeException("No Next.");
    }

  }
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
