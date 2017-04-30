package solver;


import java.util.ArrayList;
import java.util.Hashtable;

import maze.Maze;
import maze.MazeNode;

// Represents a MazeFunction that traverses and solves the maze
abstract class MazeSolver extends MazeFunction {
  MazeNode end;

  //map of links from nodes to the nodes those nodes were reached from
  Hashtable<MazeNode, MazeNode> paths;

  MazeSolver(Maze maze) {
    super(maze);

    this.end = maze.nodes().get(maze.nodes().size() - 1);

    this.paths = new Hashtable<MazeNode, MazeNode>();
    paths.put(this.start, this.start);
  }

  // Checks if this MazeSolver can perform another step in the current maze
  public boolean canStep() {
    return !this.paths.containsKey(this.end);
  }

  // Returns a list of all paths from the given node
  ArrayList<MazeNode> pathFrom(MazeNode node) {
    if (!this.paths.containsKey(node)) {
      throw new RuntimeException("Path to this node not yet found.");
    }

    ArrayList<MazeNode> ret = new ArrayList<MazeNode>();

    MazeNode to = node;
    MazeNode from = this.paths.get(node);
    ret.add(node);

    while (to != from) {
      to = from;
      from = this.paths.get(to);

      ret.add(to);
    }

    return ret;
  }
}