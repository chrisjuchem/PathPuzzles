package solver;

import maze.Maze;
import maze.MazeNode;

// Represents an automatic solving of the maze using breadth first search
public class BreadthSolver extends AutoSolver {
  public BreadthSolver(Maze maze) {
    super(maze);
  }

  //adds the node to the correct end of the worklist for this type of search
  //EFFECT: modifies this.worklist
  void addToWorklist(MazeNode n) {
    this.worklist.addToTail(n);
  }
}