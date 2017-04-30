package deque;

import java.util.Iterator;

import tester.Tester;

public class DequeTest {
  void testDeque(Tester t) {
    Deque<Integer> d1 = new Deque<Integer>();
    t.checkExpect(d1.isEmpty(), true);
    d1.addToHead(4);
    d1.addToHead(2);
    d1.addToHead(7);
    d1.addToHead(6);

    Iterator<Integer> i1 = new DequeIter<Integer>(d1);
    t.checkExpect(i1.hasNext(), true);
    t.checkExpect(i1.next(), 6);
    t.checkExpect(i1.next(), 7);
    t.checkExpect(i1.next(), 2);
    t.checkExpect(i1.hasNext(), true);
    t.checkExpect(i1.next(), 4);
    t.checkExpect(i1.hasNext(), false);


    t.checkExpect(d1.isEmpty(), false);
    t.checkExpect(d1.peek(), 6);
    d1.removeFromHead();
    t.checkExpect(d1.peek(), 7);
    d1.removeFromTail();
    t.checkExpect(d1.peek(), 7);
    d1.removeFromHead();
    t.checkExpect(d1.peek(), 2);
    t.checkExpect(d1.isEmpty(), false);
    d1.removeFromHead();
    t.checkExpect(d1.isEmpty(), true);
  }
}
