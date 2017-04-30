package solver;

import javalib.impworld.WorldScene;
import maze.Maze;
import maze.MazeNode;

// Represents a maze traversal function
public abstract class MazeFunction {
  Maze maze;
  MazeNode start;

  MazeFunction(Maze maze) {
    this.maze = maze;
    this.start = maze.nodes().get(0);
  }

  // Draws this MazeFunction accordingly onto the given WorldScene
  // using the given nodeSize
  // EFFECTS: modifies the given WorldScene
  public abstract void draw(WorldScene ws, double nodeSize);

  // Checks if this MazeFunction can perform another step in the current maze
  public abstract boolean canStep();

  // Steps this MazeFunction
  public abstract void step();
}