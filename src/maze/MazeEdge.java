package maze;

// Represents an edge of the maze from one node to another, with a weight
public class MazeEdge {
  MazeNode node1;
  MazeNode node2;
  int weight;

  MazeEdge(MazeNode node1, MazeNode node2, int weight) {
    this.node1 = node1;
    this.node2 = node2;
    this.weight = weight;
  }

  // Returns whether this node's weight is greater than that node's weight
  public boolean hasGreaterWeight(MazeEdge that) {
    return this.weight > that.weight;
  }
}

