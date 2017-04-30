package deque;

import java.util.Iterator;

public class Deque<T> implements Iterable<T> {
  Sentinel<T> header;

  public Deque() {
    this.header = new Sentinel<T>();
  }


  //determines if this deque contains no data
  boolean isEmpty() {
    return this.header.next == this.header
            && this.header.prev == this.header;
  }

  //adds the given data to the head of this deque
  public void addToHead(T data) {
    new Node<T>(data, this.header.next, this.header);
  }

  //adds the given data to the tail of this deque
  public void addToTail(T data) {
    new Node<T>(data, this.header, this.header.prev);
  }

  //removes the node from the head of the deque and returns that node's value
  public T removeFromHead() {
    if (this.isEmpty()) {
      throw new RuntimeException("Nothing to remove.");
    }
    else {
      Node<T> node = (Node<T>) this.header.next;
      node.remove();
      return node.data;
    }
  }

  //removes the node from the tail of the deque and returns that node's value
  public T removeFromTail() {
    if (this.isEmpty()) {
      throw new RuntimeException("Nothing to remove.");
    }
    else {
      Node<T> node = (Node<T>) this.header.prev;
      node.remove();
      return node.data;
    }
  }

  //returns the data of the node at the head, if present
  public T peek() {
    return this.header.next.peek();
  }

  public Iterator<T> iterator() {
    return new DequeIter<T>(this);
  }
}