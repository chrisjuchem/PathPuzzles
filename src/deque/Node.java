package deque;


class Node<T> extends ANode<T> {
  T data;

  Node(T data, ANode<T> next, ANode<T> prev) {
    this.data = data;
    this.next = next;
    this.prev = prev;

    prev.next = this;
    next.prev = this;
  }

  //removes this node from its deque
  void remove() {
    this.next.prev = this.prev;
    this.prev.next = this.next;
  }

  //returns true if the node is a sentinel
  boolean isSentinel() {
    return false;
  }

  //returns the data of the node at the head, if present
  T peek() {
    return this.data;
  }
}
