package solver;

import maze.Maze;
import maze.MazeNode;

// Represents an automatic solving of the maze using depth first search
public class DepthSolver extends AutoSolver {
  public DepthSolver(Maze maze) {
    super(maze);
  }

  //adds the node to the correct end of the worklist for this type of search
  //EFFECT: modifies this.worklist
  void addToWorklist(MazeNode n) {
    this.worklist.addToHead(n);
  }
}
