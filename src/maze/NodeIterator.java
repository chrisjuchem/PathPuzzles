package maze;

import java.util.Iterator;

//returns a nodes adjacencies in this order: above, left, below, right
//(clockwise from top)
class NodeIterator implements Iterator<MazeNode> {

  int counter; // the number of items that have been given
  MazeNode node;

  NodeIterator(MazeNode node) {
    this.node = node;
    this.counter = 0;
  }

  // Checks of this NodeIterator has a next
  public boolean hasNext() {
    return this.counter < 4;
  }

  // Returns the current next and moves the iterator forward
  // EFFECTS: changes counter
  public MazeNode next() {
    counter += 1;
    if (counter == 1) {
      return node.above;
    }
    else if (counter == 2) {
      return node.left;
    }
    else if (counter == 3) {
      return node.below;
    }
    else if (counter == 4) {
      return node.right;
    }
    else {
      throw new RuntimeException("No next.");
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}