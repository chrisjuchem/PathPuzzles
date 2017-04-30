package deque;

class Sentinel<T> extends ANode<T> {
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  //returns true if the node is a sentinel
  boolean isSentinel() {
    return true;
  }

  //returns the data of the node at the head, if present
  T peek() {
    throw new RuntimeException("Nothing to peek.");
  }
}
